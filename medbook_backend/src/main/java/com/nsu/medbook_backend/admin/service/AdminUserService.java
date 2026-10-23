package com.nsu.medbook_backend.admin.service;

import com.nsu.medbook_backend.admin.dto.AdminCreateUserRequest;
import com.nsu.medbook_backend.common.exception.ConflictException;
import com.nsu.medbook_backend.common.exception.ResourceNotFoundException;
import com.nsu.medbook_backend.doctor.entity.Doctor;
import com.nsu.medbook_backend.doctor.repository.DoctorRepository;
import com.nsu.medbook_backend.patient.entity.Patient;
import com.nsu.medbook_backend.patient.repository.PatientRepository;
import com.nsu.medbook_backend.user.entity.Role;
import com.nsu.medbook_backend.user.entity.User;
import com.nsu.medbook_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<User> listUsers(String search, Pageable pageable) {
        if (search == null || search.isBlank()) {
            return userRepository.findAll(pageable);
        }
        String term = search.trim();
        return userRepository.searchByTerm(term, pageable);
    }

    /**
     * Provisions a user with any role. Privileged roles (doctor, staff,
     * admin) can only be created here, never through public registration.
     */
    @Transactional
    public User createUser(AdminCreateUserRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("An account with this email already exists");
        }

        if (request.role() == Role.DOCTOR) {
            if (request.licenseNumber() == null || request.licenseNumber().isBlank()) {
                throw new IllegalArgumentException("License number is required for doctor accounts");
            }
            if (doctorRepository.existsByLicenseNumber(request.licenseNumber().trim())) {
                throw new ConflictException("A doctor with this license number already exists");
            }
        }

        User user = userRepository.save(User.builder()
                .username(email)
                .email(email)
                .password(passwordEncoder.encode(request.password()))
                .firstName(request.firstName().trim())
                .lastName(request.lastName().trim())
                .phone(request.phone())
                .role(request.role())
                .active(true)
                .build());

        createProfile(user, request.licenseNumber());
        return user;
    }

    @Transactional
    public User setActive(Long userId, boolean active) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.of("User", userId));
        user.setActive(active);
        return userRepository.save(user);
    }

    private void createProfile(User user, String licenseNumber) {
        switch (user.getRole()) {
            case PATIENT -> patientRepository.save(Patient.builder().user(user).build());
            case DOCTOR -> doctorRepository.save(Doctor.builder()
                    .user(user)
                    .licenseNumber(licenseNumber.trim())
                    // Experience and consultant fee are refined later through
                    // the doctor profile workflow; safe placeholders satisfy
                    // the NOT NULL columns.
                    .experience(0)
                    .consultantFee(BigDecimal.ZERO)
                    .build());
            default -> { /* ADMIN and STAFF have no profile row */ }
        }
    }
}
