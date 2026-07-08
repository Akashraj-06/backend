package com.fixly.dto;

import com.fixly.entity.User.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Read-only projection of a user's profile returned by GET /api/profile.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    private Long   id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String profileImageUrl;
    private Role   role;
}
