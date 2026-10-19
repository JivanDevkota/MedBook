package com.nsu.medbook_backend.payment.repository;

import com.nsu.medbook_backend.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByPatientId(Long patientId);
    List<Payment> findByAppointmentId(Long appointmentId);
    List<Payment> findByStatus(Payment.PaymentStatus status);
}
