package com.nsu.medbook_backend.file.entity;

import com.nsu.medbook_backend.common.entity.BaseEntity;
import com.nsu.medbook_backend.patient.entity.Patient;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "File name is required")
    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @NotBlank(message = "Storage path is required")
    @Column(name = "storage_path", nullable = false, length = 500)
    private String storagePath;

    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    @Column(nullable = false)
    private Long sizeBytes;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
}
