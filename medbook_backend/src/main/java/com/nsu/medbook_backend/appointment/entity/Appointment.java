package com.nsu.medbook_backend.appointment.entity;

import com.nsu.medbook_backend.common.entity.BaseEntity;
import com.nsu.medbook_backend.common.validation.annotation.FutureAppointment;
import com.nsu.medbook_backend.doctor.entity.Doctor;
import com.nsu.medbook_backend.patient.entity.Patient;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
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
    @FutureAppointment
    @Column(name = "appointment_date_time", nullable = false)
    private LocalDateTime appointmentDateTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;

    @Size(max = 500)
    @Column(length = 500)
    private String reason;

    @Size(max = 1000)
    @Column(name = "doctor_notes", length = 1000)
    private String doctorNotes;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    public enum AppointmentStatus {
        SCHEDULED,
        CONFIRMED,
        COMPLETED,
        CANCELLED,
        NO_SHOW
    }
}
