package com.nsu.medbook_backend.medicalrecord.entity;

import com.nsu.medbook_backend.appointment.entity.Appointment;
import com.nsu.medbook_backend.common.entity.BaseEntity;
import com.nsu.medbook_backend.doctor.entity.Doctor;
import com.nsu.medbook_backend.patient.entity.Patient;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "medical_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 200)
    @Column(length = 200)
    private String diagnosis;

    @Size(max = 2000)
    @Column(length = 2000)
    private String symptoms;

    @Size(max = 2000)
    @Column(name = "treatment_plan", length = 2000)
    private String treatmentPlan;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", unique = true)
    private Appointment appointment;
}
