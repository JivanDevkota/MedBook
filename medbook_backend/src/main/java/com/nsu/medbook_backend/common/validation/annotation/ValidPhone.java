package com.nsu.medbook_backend.common.validation.annotation;

import com.nsu.medbook_backend.common.validation.validator.PhoneValidator;
import com.nsu.medbook_backend.common.validation.validator.UniqueEmailValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;

@Documented
@Constraint(validatedBy = PhoneValidator.class)
public @interface ValidPhone {

    String message() default "Invalid Phone number";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
