package com.nsu.medbook_backend.patient.entity;

import com.nsu.medbook_backend.common.entity.BaseEntity;
import com.nsu.medbook_backend.common.validation.annotation.ValidPhone;
import com.nsu.medbook_backend.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Patient extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate birthDate;
    private String gender;
    private String bloodType;

    @ValidPhone
    private String contact;

    @OneToOne(cascade = CascadeType.ALL)
    private User user;
}
