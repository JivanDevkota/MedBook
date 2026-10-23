package com.nsu.medbook_backend.auth.service;

import com.nsu.medbook_backend.auth.dto.AuthSession;
import com.nsu.medbook_backend.auth.dto.LoginRequest;
import com.nsu.medbook_backend.auth.dto.RegisterRequest;
import com.nsu.medbook_backend.auth.dto.UserResponse;
import com.nsu.medbook_backend.common.exception.ConflictException;
import com.nsu.medbook_backend.patient.entity.Patient;
import com.nsu.medbook_backend.patient.repository.PatientRepository;
import com.nsu.medbook_backend.security.MyUser;
import com.nsu.medbook_backend.security.jwt.JwtTokenProvider;
import com.nsu.medbook_backend.user.entity.Role;
import com.nsu.medbook_backend.user.entity.User;
import com.nsu.medbook_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Public self-registration always creates a PATIENT account; privileged
     * roles are provisioned exclusively through the admin module.
     */
    @Transactional
    public AuthSession register(RegisterRequest request) {
        String email = normalizeEmail(request.email());

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("An account with this email already exists");
        }

        User user = userRepository.save(User.builder()
                .username(email)
                .email(email)
                .password(passwordEncoder.encode(request.password()))
                .firstName(request.firstName().trim())
                .lastName(request.lastName().trim())
                .phone(request.phone())
                .role(Role.PATIENT)
                .active(true)
                .build());

        patientRepository.save(Patient.builder()
                .user(user)
                .contact(request.phone())
                .build());

        return issueSession(user);
    }

    @Transactional(readOnly = true)
    public AuthSession login(LoginRequest request) {
        String email = normalizeEmail(request.email());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.password()));

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        return issueSession(user);
    }

    /**
     * Exchanges a valid refresh token for a new session, rotating the refresh
     * token (sliding expiry) on every exchange.
     */
    @Transactional(readOnly = true)
    public AuthSession refresh(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BadCredentialsException("Refresh token is missing");
        }

        String username;
        try {
            username = jwtTokenProvider.extractUsername(refreshToken);
        } catch (Exception ex) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        User user = userRepository.findByEmailIgnoreCase(username)
                .or(() -> userRepository.findByUsername(username))
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));

        if (!jwtTokenProvider.isRefreshTokenValid(refreshToken, new MyUser(user))) {
            throw new BadCredentialsException("Invalid or expired refresh token");
        }

        return issueSession(user);
    }

    private AuthSession issueSession(User user) {
        MyUser userDetails = new MyUser(user);
        return new AuthSession(
                UserResponse.from(user),
                jwtTokenProvider.generateAccessToken(userDetails),
                jwtTokenProvider.generateRefreshToken(userDetails)
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
