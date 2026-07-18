package com.nsu.medbook_backend.doctor.entity;

import com.nsu.medbook_backend.common.entity.BaseEntity;
import com.nsu.medbook_backend.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class Doctor extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String licenceNumber;
    private int experience;
    private BigDecimal consultant_fee;
    private String bio;

    @OneToOne(cascade = CascadeType.ALL)
    private User user;
}
