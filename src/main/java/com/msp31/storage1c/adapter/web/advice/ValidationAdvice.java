package com.msp31.storage1c.adapter.web.advice;

import com.msp31.storage1c.adapter.web.annotation.ApiAdvice;
import com.msp31.storage1c.common.constant.Status;
import com.msp31.storage1c.domain.dto.response.ResponseModel;
import com.msp31.storage1c.domain.dto.response.ValidationErrorResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
}
