package com.fixly.controller;

import com.fixly.dto.ChangePasswordDto;
import com.fixly.dto.UpdateProfileDto;
import com.fixly.dto.UserProfileDto;
import com.fixly.security.CustomUserDetails;
import com.fixly.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<UserProfileDto> getProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(profileService.getProfile(userDetails.getId()));
    }

    @PutMapping
    public ResponseEntity<UserProfileDto> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpdateProfileDto dto
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(profileService.updateProfile(userDetails.getId(), dto));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ChangePasswordDto dto
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            String message = profileService.changePassword(userDetails.getId(), dto);
            return ResponseEntity.ok(Map.of("message", message));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }
}
