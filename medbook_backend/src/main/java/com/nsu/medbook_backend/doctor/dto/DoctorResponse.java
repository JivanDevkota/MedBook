package com.nsu.medbook_backend.doctor.dto;

import com.nsu.medbook_backend.doctor.entity.Doctor;

import java.math.BigDecimal;

public record DoctorResponse(
        Long id,
        Long userId,
        String fullName,
        String email,
        String licenseNumber,
        int experience,
        BigDecimal consultantFee,
        String specialization,
        String bio
) {
    public static DoctorResponse from(Doctor doctor) {
        return new DoctorResponse(
                doctor.getId(),
                doctor.getUser().getId(),
                doctor.getUser().getFirstName() + " " + doctor.getUser().getLastName(),
                doctor.getUser().getEmail(),
                doctor.getLicenseNumber(),
                doctor.getExperience(),
                doctor.getConsultantFee(),
                doctor.getSpecialization(),
                doctor.getBio()
        );
    }
}
