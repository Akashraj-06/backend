package com.fixly.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Payload accepted by PUT /api/profile.
 * Only editable fields are included — email and role cannot be changed here.
 */
@Data
public class UpdateProfileDto {

    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Pattern(
        regexp = "^$|^[0-9+\\-\\s()]{7,20}$",
        message = "Phone number must be 7–20 characters (digits, +, -, spaces, parentheses)"
    )
    private String phone;

    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @Size(max = 500, message = "Profile image URL must not exceed 500 characters")
    private String profileImageUrl;
}
