package com.nsu.medbook_backend.auth.service;

import com.nsu.medbook_backend.auth.dto.AuthResponse;
import com.nsu.medbook_backend.auth.dto.LoginRequest;
import com.nsu.medbook_backend.auth.dto.RegisterRequest;
import com.nsu.medbook_backend.auth.dto.UserResponse;
import com.nsu.medbook_backend.common.exception.ConflictException;
import com.nsu.medbook_backend.common.exception.ResourceNotFoundException;
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

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("An account with this email already exists");
        }

        User user = User.builder()
                .username(email)
                .email(email)
                .password(passwordEncoder.encode(request.password()))
                .firstName(request.firstName().trim())
                .lastName(request.lastName().trim())
                .phone(request.phone())
                .role(Role.PATIENT)
                .active(true)
                .build();

        User savedUser = userRepository.save(user);

        patientRepository.save(Patient.builder()
                .user(savedUser)
                .contact(request.phone())
                .build());

        return buildAuthResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.password()));

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return buildAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse refresh(String refreshToken) {
        String username;
        try {
            username = jwtTokenProvider.extractUsername(refreshToken);
        } catch (Exception ex) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        User user = userRepository.findByEmailIgnoreCase(username)
                .or(() -> userRepository.findByUsername(username))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!jwtTokenProvider.isRefreshTokenValid(refreshToken, new MyUser(user))) {
            throw new BadCredentialsException("Invalid or expired refresh token");
        }

        return buildAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String identifier) {
        User user = userRepository.findByEmailIgnoreCase(identifier)
                .or(() -> userRepository.findByUsername(identifier))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserResponse.from(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        MyUser userDetails = new MyUser(user);
        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        return AuthResponse.of(
                accessToken,
                refreshToken,
                jwtTokenProvider.getAccessTokenExpirationSeconds(),
                UserResponse.from(user)
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
