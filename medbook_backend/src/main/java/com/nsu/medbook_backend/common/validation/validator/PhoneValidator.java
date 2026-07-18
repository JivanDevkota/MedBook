package com.nsu.medbook_backend.common.validation.validator;

import com.nsu.medbook_backend.common.validation.annotation.ValidPhone;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneValidator implements ConstraintValidator<ValidPhone, String> {

    private static final String PHONE_REGEX = "^(\\+94|0)(7[01245678])[0-9]{7}$";

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext constraintValidatorContext) {
        if (phone == null || phone.isBlank()) {
            return true;
        }
        return phone.matches(PHONE_REGEX);
    }
}
