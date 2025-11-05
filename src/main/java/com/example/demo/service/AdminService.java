package com.example.demo.service;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.EmployeeRequest;
import com.example.demo.dto.ProfileUpdateRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public ApiResponse addEmployee(EmployeeRequest request) {
        // Generate username from name (lowercase, remove spaces)
        String username = request.getName().toLowerCase().replaceAll("\\s+", "") + 
                         System.currentTimeMillis() % 10000;
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        // Generate reset token
        String resetToken = UUID.randomUUID().toString();
        LocalDateTime tokenExpiry = LocalDateTime.now().plusHours(24);

        // Split name into first and last name
        String[] nameParts = request.getName().trim().split("\\s+", 2);
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        User employee = User.builder()
                .username(username)
                .email(request.getEmail())
                .password(passwordEncoder.encode(UUID.randomUUID().toString())) // Temporary random password
                .role(Role.EMPLOYEE)
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(request.getPhone())
                .position(request.getPosition())
                .department(request.getDepartment())
                .isActive(true)
                .isPasswordChanged(false)
                .resetToken(resetToken)
                .resetTokenExpiry(tokenExpiry)
                .build();

        userRepository.save(employee);

        // Send email with token
        try {
            emailService.sendEmployeeCreationEmail(employee.getEmail(), employee.getUsername(), resetToken);
        } catch (Exception e) {
            System.err.println("Failed to send email, but employee created: " + e.getMessage());
        }

        return ApiResponse.builder()
                .success(true)
                .message("Employee added successfully. Activation email sent.")
                .data(convertToUserResponse(employee))
                .build();
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllEmployees() {
        return userRepository.findByRole(Role.EMPLOYEE).stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllCustomers() {
        return userRepository.findByRole(Role.CUSTOMER).stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return convertToUserResponse(user);
    }

    @Transactional
    public ApiResponse deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (user.getRole() == Role.ADMIN) {
            throw new BadRequestException("Cannot deactivate admin user");
        }

        user.setIsActive(false);
        userRepository.save(user);

        return ApiResponse.builder()
                .success(true)
                .message("User deactivated successfully")
                .build();
    }

    @Transactional
    public ApiResponse activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setIsActive(true);
        userRepository.save(user);

        return ApiResponse.builder()
                .success(true)
                .message("User activated successfully")
                .build();
    }

    @Transactional
    public ApiResponse resendEmployeeActivationEmail(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (user.getRole() != Role.EMPLOYEE) {
            throw new BadRequestException("User is not an employee");
        }

        // Generate new reset token
        String resetToken = UUID.randomUUID().toString();
        LocalDateTime tokenExpiry = LocalDateTime.now().plusHours(24);

        user.setResetToken(resetToken);
        user.setResetTokenExpiry(tokenExpiry);
        userRepository.save(user);

        // Resend email
        emailService.sendEmployeeCreationEmail(user.getEmail(), user.getUsername(), resetToken);

        return ApiResponse.builder()
                .success(true)
                .message("Activation email resent successfully")
                .build();
    }

    @Transactional
    public ApiResponse updateEmployee(Long id, EmployeeRequest request) {
        User employee = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        if (employee.getRole() != Role.EMPLOYEE) {
            throw new BadRequestException("User is not an employee");
        }

        // Check if email is being changed and already exists
        if (!employee.getEmail().equals(request.getEmail()) && 
            userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        // Split name into first and last name
        String[] nameParts = request.getName().trim().split("\\s+", 2);
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        // Update employee details
        employee.setEmail(request.getEmail());
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setPhoneNumber(request.getPhone());
        employee.setPosition(request.getPosition());
        employee.setDepartment(request.getDepartment());

        userRepository.save(employee);

        return ApiResponse.builder()
                .success(true)
                .message("Employee updated successfully")
                .data(convertToUserResponse(employee))
                .build();
    }

    @Transactional
    public ApiResponse deleteEmployee(Long id) {
        User employee = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        if (employee.getRole() != Role.EMPLOYEE) {
            throw new BadRequestException("User is not an employee");
        }

        if (employee.getRole() == Role.ADMIN) {
            throw new BadRequestException("Cannot delete admin user");
        }

        userRepository.delete(employee);

        return ApiResponse.builder()
                .success(true)
                .message("Employee deleted successfully")
                .build();
    }

    @Transactional
    public UserResponse updateAdminProfile(ProfileUpdateRequest request, String token) {
        // Extract username from token
        String bearerToken = token.replace("Bearer ", "");
        String username = jwtTokenProvider.getUsernameFromToken(bearerToken);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if email is being changed and already exists
        if (!user.getEmail().equals(request.getEmail()) && 
            userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        // Check if username is being changed and already exists
        if (!user.getUsername().equals(request.getUsername()) && 
            userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }

        // Update basic profile information
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhone());
        user.setProfileImage(request.getProfileImage());

        // Handle password change if provided
        if (request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
            if (request.getCurrentPassword() == null || request.getCurrentPassword().isEmpty()) {
                throw new BadRequestException("Current password is required to change password");
            }

            // Verify current password
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new BadRequestException("Current password is incorrect");
            }

            // Update password
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        userRepository.save(user);

        return convertToUserResponse(user);
    }

    private UserResponse convertToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .position(user.getPosition())
                .department(user.getDepartment())
                .profileImage(user.getProfileImage())
                .isActive(user.getIsActive())
                .isPasswordChanged(user.getIsPasswordChanged())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
