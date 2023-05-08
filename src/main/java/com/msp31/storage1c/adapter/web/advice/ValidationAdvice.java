package com.msp31.storage1c.adapter.web.advice;

import com.msp31.storage1c.adapter.web.annotation.ApiAdvice;
import com.msp31.storage1c.common.constant.Status;
import com.msp31.storage1c.domain.dto.response.ResponseModel;
import com.msp31.storage1c.domain.dto.response.ValidationErrorResponse;
import com.msp31.storage1c.domain.entity.repo.Repo;
import jakarta.validation.ConstraintViolationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.HashSet;
import java.util.stream.Collectors;

@ApiAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ValidationAdvice {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ResponseModel<ValidationErrorResponse>> notValid(MethodArgumentNotValidException e) {
        var fields = e.getFieldErrors().stream().map(FieldError::getField).collect(Collectors.toSet());
        return ResponseEntity
                .status(Status.VALIDATION_ERROR.getHttpCode())
                .body(ResponseModel.withStatus(Status.VALIDATION_ERROR, new ValidationErrorResponse(fields)));
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<ResponseModel<ValidationErrorResponse>> constraintViolation(ConstraintViolationException e) {
        var fields = e.getConstraintViolations().stream().map((violation) -> {
            var path = violation.getPropertyPath().toString();
            return path.substring(path.lastIndexOf('.') + 1);
        }).collect(Collectors.toSet());
        return ResponseEntity
                .status(Status.VALIDATION_ERROR.getHttpCode())
                .body(ResponseModel.withStatus(Status.VALIDATION_ERROR, new ValidationErrorResponse(fields)));
    }

    @ExceptionHandler({MissingServletRequestPartException.class})
    ResponseEntity<ResponseModel<ValidationErrorResponse>> requestPartMissing(MissingServletRequestPartException e) {
        var fields = new HashSet<String>();
        fields.add(e.getRequestPartName());
        return ResponseEntity
                .status(Status.VALIDATION_ERROR.getHttpCode())
                .body(ResponseModel.withStatus(Status.VALIDATION_ERROR, new ValidationErrorResponse(fields)));
    }
}
