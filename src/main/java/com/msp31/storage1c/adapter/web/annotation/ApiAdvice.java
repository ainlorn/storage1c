package com.msp31.storage1c.adapter.web.annotation;

import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target(TYPE)
@Retention(RUNTIME)
@RestControllerAdvice(basePackages = "com.msp31.storage1c.adapter.web")
public @interface ApiAdvice {
}
