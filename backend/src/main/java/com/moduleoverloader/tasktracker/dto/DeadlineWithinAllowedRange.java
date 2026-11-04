package com.moduleoverloader.tasktracker.dto;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DeadlineWithinAllowedRangeValidator.class)
@Target({ElementType.FIELD, ElementType.RECORD_COMPONENT, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface DeadlineWithinAllowedRange {
    String message() default "Deadline must be between 2000 and 2099";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
