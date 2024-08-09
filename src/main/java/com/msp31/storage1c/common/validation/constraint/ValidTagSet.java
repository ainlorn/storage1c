package com.msp31.storage1c.common.validation.constraint;

import com.msp31.storage1c.common.validation.validator.ValidTagSetValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = ValidTagSetValidator.class)
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
public @interface ValidTagSet {
    String message() default "Tag set invalid!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
