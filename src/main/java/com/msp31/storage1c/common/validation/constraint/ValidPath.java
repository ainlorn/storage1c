package com.msp31.storage1c.common.validation.constraint;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Pattern(regexp = "^[a-zA-Z0-9а-яА-Я /._\\-%@^;,'+=()\\[\\]{}&$#№—–]+$")
@Size(min = 1, max = 255)
@Constraint(validatedBy = {})
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPath {
    String message() default "Not a valid path!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
