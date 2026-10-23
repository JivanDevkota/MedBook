package com.nsu.medbook_backend.doctor.repository;

import com.nsu.medbook_backend.doctor.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByUserId(Long userId);
    List<Doctor> findBySpecializationContainingIgnoreCase(String specialization);
    boolean existsByLicenseNumber(String licenseNumber);
}
