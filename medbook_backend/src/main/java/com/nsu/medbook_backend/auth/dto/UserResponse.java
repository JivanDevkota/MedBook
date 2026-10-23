package com.nsu.medbook_backend.auth.dto;

import com.nsu.medbook_backend.user.entity.Role;
import com.nsu.medbook_backend.user.entity.User;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        String phone,
        Role role,
        boolean active,
        LocalDateTime createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getRole(),
                user.isActive(),
                user.getCreatedAt()
        );
    }
}
