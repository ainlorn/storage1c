package com.msp31.storage1c.common.validation.validator;

import com.msp31.storage1c.common.validation.constraint.AsciiString;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AsciiStringValidator implements ConstraintValidator<AsciiString, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || value.chars().allMatch(c -> c < 128);
    }
}
