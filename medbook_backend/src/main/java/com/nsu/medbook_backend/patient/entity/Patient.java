package com.nsu.medbook_backend.patient.entity;

import com.nsu.medbook_backend.appointment.entity.Appointment;
import com.nsu.medbook_backend.common.entity.BaseEntity;
import com.nsu.medbook_backend.common.validation.annotation.ValidPhone;
import com.nsu.medbook_backend.file.entity.FileEntity;
import com.nsu.medbook_backend.medicalrecord.entity.MedicalRecord;
import com.nsu.medbook_backend.payment.entity.Payment;
import com.nsu.medbook_backend.prescription.entity.Prescription;
import com.nsu.medbook_backend.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Past(message = "Birth date must be in the past")
    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    @Size(max = 5)
    @Column(name = "blood_type", length = 5)
    private String bloodType;

    @ValidPhone
    @Column(length = 15)
    private String contact;

    @Column(length = 500)
    private String address;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Appointment> appointments = new ArrayList<>();

    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MedicalRecord> medicalRecords = new ArrayList<>();

    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Prescription> prescriptions = new ArrayList<>();

    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Payment> payments = new ArrayList<>();

    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FileEntity> files = new ArrayList<>();

    public enum Gender {
        MALE,
        FEMALE,
        OTHER
    }
}
