package com.nsu.medbook_backend.common.validation.validator;

import com.nsu.medbook_backend.common.validation.annotation.FutureAppointment;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class FutureAppointmentValidator implements ConstraintValidator<FutureAppointment, LocalDateTime> {

    @Override
    public boolean isValid(LocalDateTime dateTime, ConstraintValidatorContext constraintValidatorContext) {

        if (dateTime == null) {
            return true;
        }

        return dateTime.isAfter(LocalDateTime.now());
    }
}
