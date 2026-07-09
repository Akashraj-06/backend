package com.fixly.service;

import com.fixly.dto.ChangePasswordDto;
import com.fixly.dto.UpdateProfileDto;
import com.fixly.dto.UserProfileDto;
import com.fixly.entity.User;
import com.fixly.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserProfileDto getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return mapToDto(user);
    }

    @Transactional
    public UserProfileDto updateProfile(Long userId, UpdateProfileDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (dto.getName() != null && !dto.getName().isBlank()) {
            user.setName(dto.getName());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getAddress() != null) {
            user.setAddress(dto.getAddress());
        }
        if (dto.getProfileImageUrl() != null) {
            user.setProfileImageUrl(dto.getProfileImageUrl());
        }

        User savedUser = userRepository.save(user);
        return mapToDto(savedUser);
    }

    private UserProfileDto mapToDto(User user) {
        return UserProfileDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .profileImageUrl(user.getProfileImageUrl())
                .role(user.getRole())
                .build();
    }

    /**
     * Changes the authenticated user's password after strict ordered validation.
     * Returns a success message string. Never exposes the hash in the response.
     */
    @Transactional
    public String changePassword(Long userId, ChangePasswordDto dto) {
        // 1. Required field checks
        if (dto.getCurrentPassword() == null || dto.getCurrentPassword().isBlank()) {
            throw new IllegalArgumentException("Current password is required");
        }
        if (dto.getNewPassword() == null || dto.getNewPassword().isBlank()) {
            throw new IllegalArgumentException("New password is required");
        }
        if (dto.getConfirmPassword() == null || dto.getConfirmPassword().isBlank()) {
            throw new IllegalArgumentException("Confirm password is required");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 2. Verify current password
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Incorrect current password");
        }

        // 3. Validate new password strength
        String np = dto.getNewPassword();
        if (np.length() < 8) {
            throw new IllegalArgumentException("New password must be at least 8 characters");
        }
        if (!np.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("New password must contain at least one uppercase letter");
        }
        if (!np.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("New password must contain at least one lowercase letter");
        }
        if (!np.matches(".*\\d.*")) {
            throw new IllegalArgumentException("New password must contain at least one number");
        }

        // 4. Confirm password match
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match");
        }

        // 5. Prevent reuse of the current password
        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("New password must be different from the current password");
        }

        // Encode and persist
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        return "Password changed successfully";
    }
}
