package com.nsu.medbook_backend.admin.dto;

import com.nsu.medbook_backend.common.validation.annotation.ValidPhone;
import com.nsu.medbook_backend.user.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AdminCreateUserRequest(
        @NotBlank(message = "First name is required")
        @Size(max = 50, message = "First name must not exceed 50 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 50, message = "Last name must not exceed 50 characters")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 100, message = "Email must not exceed 100 characters")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        String password,

        @ValidPhone
        String phone,

        @NotNull(message = "Role is required")
        Role role,

        @Size(max = 50, message = "License number must not exceed 50 characters")
        String licenseNumber
) {
}
