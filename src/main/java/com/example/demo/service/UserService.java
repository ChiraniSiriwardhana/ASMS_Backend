package com.example.demo.service;

import com.example.demo.dto.ProfileUpdateRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserResponse getCurrentUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .profileImage(user.getProfileImage())
                .isActive(user.getIsActive())
                .isPasswordChanged(user.getIsPasswordChanged())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Transactional
    public UserResponse updateProfile(String username, ProfileUpdateRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Update basic information
        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            // Check if new username is already taken by another user
            if (!request.getUsername().equals(user.getUsername())) {
                userRepository.findByUsername(request.getUsername())
                        .ifPresent(u -> {
                            throw new IllegalArgumentException("Username already taken");
                        });
                user.setUsername(request.getUsername());
            }
        }

        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            // Check if new email is already taken by another user
            if (!request.getEmail().equals(user.getEmail())) {
                userRepository.findByEmail(request.getEmail())
                        .ifPresent(u -> {
                            throw new IllegalArgumentException("Email already taken");
                        });
                user.setEmail(request.getEmail());
            }
        }

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }

        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }

        if (request.getProfileImage() != null) {
            user.setProfileImage(request.getProfileImage());
        }

        // Update password if provided
        if (request.getCurrentPassword() != null && !request.getCurrentPassword().isEmpty() &&
                request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
            
            // Verify current password
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new IllegalArgumentException("Current password is incorrect");
            }

            // Validate new password length
            if (request.getNewPassword().length() < 6) {
                throw new IllegalArgumentException("New password must be at least 6 characters long");
            }

            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        User updatedUser = userRepository.save(user);

        return UserResponse.builder()
                .id(updatedUser.getId())
                .username(updatedUser.getUsername())
                .email(updatedUser.getEmail())
                .role(updatedUser.getRole().name())
                .firstName(updatedUser.getFirstName())
                .lastName(updatedUser.getLastName())
                .phoneNumber(updatedUser.getPhoneNumber())
                .address(updatedUser.getAddress())
                .profileImage(updatedUser.getProfileImage())
                .isActive(updatedUser.getIsActive())
                .isPasswordChanged(updatedUser.getIsPasswordChanged())
                .createdAt(updatedUser.getCreatedAt())
                .build();
    }
}

