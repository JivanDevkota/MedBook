package com.nsu.medbook_backend.appointment.entity;

import com.nsu.medbook_backend.common.entity.BaseEntity;
import com.nsu.medbook_backend.doctor.entity.Doctor;
import com.nsu.medbook_backend.medicalrecord.entity.MedicalRecord;
import com.nsu.medbook_backend.patient.entity.Patient;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_appointments_doctor_slot",
                columnNames = {"doctor_id", "appointment_date_time"}),
        indexes = {
                @Index(name = "idx_appointments_patient", columnList = "patient_id"),
                @Index(name = "idx_appointments_date_time", columnList = "appointment_date_time")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Appointment date time is required")
    @Column(name = "appointment_date_time", nullable = false)
    private LocalDateTime appointmentDateTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;

    @Size(max = 500, message = "Reason must not exceed 500 characters")
    @Column(length = 500)
    private String reason;

    @Size(max = 1000, message = "Doctor notes must not exceed 1000 characters")
    @Column(name = "doctor_notes", length = 1000)
    private String doctorNotes;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @OneToOne(mappedBy = "appointment", fetch = FetchType.LAZY)
    private MedicalRecord medicalRecord;

    public enum AppointmentStatus {
        SCHEDULED,
        CONFIRMED,
        COMPLETED,
        CANCELLED,
        NO_SHOW
    }
}
