package com.nsu.medbook_backend.doctor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record DoctorRequest(
        @NotNull(message = "User id is required")
        Long userId,

        @NotBlank(message = "License number is required")
        @Size(max = 50, message = "License number must not exceed 50 characters")
        String licenseNumber,

        @PositiveOrZero(message = "Experience must be zero or positive")
        int experience,

        @NotNull(message = "Consultant fee is required")
        @PositiveOrZero(message = "Consultant fee must be zero or positive")
        BigDecimal consultantFee,

        @Size(max = 100, message = "Specialization must not exceed 100 characters")
        String specialization,

        @Size(max = 1000, message = "Bio must not exceed 1000 characters")
        String bio
) {
}
