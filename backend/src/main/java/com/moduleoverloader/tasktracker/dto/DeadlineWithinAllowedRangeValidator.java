package com.moduleoverloader.tasktracker.dto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class DeadlineWithinAllowedRangeValidator
        implements ConstraintValidator<DeadlineWithinAllowedRange, LocalDateTime> {

    @Override
    public boolean isValid(LocalDateTime deadline, ConstraintValidatorContext context) {
        return deadline == null || (deadline.getYear() >= 2000 && deadline.getYear() < 2100);
    }
}
