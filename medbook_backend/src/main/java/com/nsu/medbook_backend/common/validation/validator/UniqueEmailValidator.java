package com.nsu.medbook_backend.common.validation.validator;

import com.nsu.medbook_backend.common.validation.annotation.UniqueEmail;
import com.nsu.medbook_backend.user.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    private final UserRepository userRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        if (email == null || email.isBlank()) {
            return true;
        }
        return !userRepository.existsByEmail(email);
    }
}
