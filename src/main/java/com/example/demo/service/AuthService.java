package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.email}")
    private String adminEmail;

    @Transactional
    public void initializeAdmin() {
        if (!userRepository.existsByUsername(adminUsername)) {
            User admin = User.builder()
                    .username(adminUsername)
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .role(Role.ADMIN)
                    .isActive(true)
                    .isPasswordChanged(true)
                    .build();
            userRepository.save(admin);
            System.out.println("Admin user initialized successfully");
        }
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        // Find user by username, email, or phone number
        User user = userRepository.findByUsername(request.getUsername())
                .or(() -> userRepository.findByEmail(request.getUsername()))
                .or(() -> userRepository.findByPhoneNumber(request.getUsername()))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if user account is active
        if (!user.getIsActive()) {
            throw new BadRequestException("Account is deactivated. Please contact administrator.");
        }

        // Authenticate using the actual username from the database
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);

        return LoginResponse.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .profileImage(user.getProfileImage())
                .message("Login successful")
                .build();
    }

    @Transactional
    public ApiResponse customerSignup(CustomerSignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        User customer = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CUSTOMER)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .isActive(true)
                .isPasswordChanged(true)
                .build();

        userRepository.save(customer);

        return ApiResponse.builder()
                .success(true)
                .message("Customer registered successfully")
                .build();
    }

    @Transactional(readOnly = true)
    public ApiResponse verifyResetToken(String token) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid token"));

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Token has expired. Please request a new activation link.");
        }

        return ApiResponse.builder()
                .success(true)
                .message("Token is valid. Please set your password using the POST /api/auth/set-password endpoint.")
                .data(UserResponse.builder()
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .build())
                .build();
    }

    @Transactional
    public ApiResponse setPassword(SetPasswordRequest request) {
        User user = userRepository.findByResetToken(request.getToken())
                .orElseThrow(() -> new BadRequestException("Invalid or expired token"));

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Token has expired");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setIsPasswordChanged(true);
        user.setResetToken(null);
        user.setResetTokenExpiry(null);

        userRepository.save(user);

        return ApiResponse.builder()
                .success(true)
                .message("Password set successfully. You can now login.")
                .build();
    }

    @Transactional
    public ApiResponse changePassword(ChangePasswordRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setIsPasswordChanged(true);
        userRepository.save(user);

        // Send email notification
        emailService.sendPasswordChangedEmail(user.getEmail(), user.getUsername());

        return ApiResponse.builder()
                .success(true)
                .message("Password changed successfully")
                .build();
    }

    @Transactional
    public ApiResponse forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("No account found with this email address"));

        // Generate reset token
        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(24)); // Token valid for 24 hours

        userRepository.save(user);

        // Send password reset email
        emailService.sendPasswordResetEmail(user.getEmail(), user.getUsername(), resetToken);

        return ApiResponse.builder()
                .success(true)
                .message("Password reset link has been sent to your email address")
                .build();
    }

    @Transactional
    public ApiResponse resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByResetToken(request.getToken())
                .orElseThrow(() -> new BadRequestException("Invalid or expired reset token"));

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Reset token has expired. Please request a new password reset link.");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setIsPasswordChanged(true);
        user.setResetToken(null);
        user.setResetTokenExpiry(null);

        userRepository.save(user);

        // Send confirmation email
        emailService.sendPasswordResetSuccessEmail(user.getEmail(), user.getUsername());

        return ApiResponse.builder()
                .success(true)
                .message("Password has been reset successfully. You can now login with your new password.")
                .build();
    }
}

