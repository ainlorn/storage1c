package com.msp31.storage1c.common.validation.validator;

import com.msp31.storage1c.common.constant.RegexConstants;
import com.msp31.storage1c.common.validation.constraint.ValidTagSet;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;
import java.util.regex.Pattern;

public class ValidTagSetValidator implements ConstraintValidator<ValidTagSet, Set<String>> {
    private static final Pattern TAG_PATTERN = Pattern.compile(RegexConstants.TAG_REGEX);

    @Override
    public void initialize(ValidTagSet constraintAnnotation) {
    }

    @Override
    public boolean isValid(Set<String> strings, ConstraintValidatorContext constraintValidatorContext) {
        return strings == null || strings.stream().allMatch(str ->
                str != null
                        && !str.trim().isEmpty()
                        && TAG_PATTERN.matcher(str.trim()).matches());
    }
}
