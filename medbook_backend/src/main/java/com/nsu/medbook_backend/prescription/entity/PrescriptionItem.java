package com.nsu.medbook_backend.prescription.entity;

import com.nsu.medbook_backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "prescription_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Medication name is required")
    @Column(name = "medication_name", nullable = false, length = 150)
    private String medicationName;

    @NotBlank(message = "Dosage is required")
    @Column(nullable = false, length = 100)
    private String dosage;

    @NotBlank(message = "Frequency is required")
    @Column(nullable = false, length = 100)
    private String frequency;

    @Min(value = 1, message = "Duration must be at least 1 day")
    @Column(name = "duration_days", nullable = false)
    private int durationDays;

    @Column(length = 500)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;
}
