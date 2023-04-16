package com.msp31.storage1c.domain.dto.response;

import lombok.Value;

import java.util.Set;

@Value
public class ValidationErrorResponse {
    Set<String> invalidFields;
}
