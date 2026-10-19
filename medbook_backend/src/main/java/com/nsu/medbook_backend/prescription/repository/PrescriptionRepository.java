package com.nsu.medbook_backend.prescription.repository;

import com.nsu.medbook_backend.prescription.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByPatientId(Long patientId);
    List<Prescription> findByDoctorId(Long doctorId);
    List<Prescription> findByMedicalRecordId(Long medicalRecordId);
}
