package com.nsu.medbook_backend.medicalrecord.entity;

import com.nsu.medbook_backend.appointment.entity.Appointment;
import com.nsu.medbook_backend.common.entity.BaseEntity;
import com.nsu.medbook_backend.doctor.entity.Doctor;
import com.nsu.medbook_backend.patient.entity.Patient;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "medical_records", indexes = {
        @Index(name = "idx_medical_records_patient", columnList = "patient_id"),
        @Index(name = "idx_medical_records_doctor", columnList = "doctor_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 200, message = "Diagnosis must not exceed 200 characters")
    @Column(length = 200)
    private String diagnosis;

    @Size(max = 2000, message = "Symptoms must not exceed 2000 characters")
    @Column(length = 2000)
    private String symptoms;

    @Size(max = 2000, message = "Treatment plan must not exceed 2000 characters")
    @Column(name = "treatment_plan", length = 2000)
    private String treatmentPlan;

    @NotNull(message = "Record date is required")
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
