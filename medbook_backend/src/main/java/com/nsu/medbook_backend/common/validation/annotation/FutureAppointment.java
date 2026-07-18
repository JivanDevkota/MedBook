package com.nsu.medbook_backend.common.validation.annotation;

import com.nsu.medbook_backend.common.validation.validator.FutureAppointmentValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FutureAppointmentValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FutureAppointment {

    String message() default "Appointment date  must be in the future";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
