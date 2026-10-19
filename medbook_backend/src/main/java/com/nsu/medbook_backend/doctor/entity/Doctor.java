package com.nsu.medbook_backend.doctor.entity;

import com.nsu.medbook_backend.appointment.entity.Appointment;
import com.nsu.medbook_backend.common.entity.BaseEntity;
import com.nsu.medbook_backend.medicalrecord.entity.MedicalRecord;
import com.nsu.medbook_backend.prescription.entity.Prescription;
import com.nsu.medbook_backend.schedule.entity.Schedule;
import com.nsu.medbook_backend.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "License number is required")
    @Column(name = "license_number", nullable = false, unique = true, length = 50)
    private String licenseNumber;

    @PositiveOrZero(message = "Experience must be zero or positive")
    @Column(nullable = false)
    private int experience;

    @PositiveOrZero(message = "Consultant fee must be zero or positive")
    @Column(name = "consultant_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal consultantFee;

    @Column(length = 1000)
    private String bio;

    @Column(length = 100)
    private String specialization;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "doctor", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Appointment> appointments = new ArrayList<>();

    @OneToMany(mappedBy = "doctor", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Schedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "doctor", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MedicalRecord> medicalRecords = new ArrayList<>();

    @OneToMany(mappedBy = "doctor", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Prescription> prescriptions = new ArrayList<>();
}
