package com.fixly.dto;

import com.fixly.entity.User.Role;
import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class RegisterRequest {
    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    private String phone;

    @NotNull
    private Role role;

    // For Worker registration
    private Long categoryId;
    private Double latitude;
    private Double longitude;
}
