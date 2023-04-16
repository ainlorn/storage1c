package com.msp31.storage1c.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.msp31.storage1c.common.constant.Status;
import lombok.Value;

@Value
@JsonSerialize
@JsonInclude(JsonInclude.Include.ALWAYS)
public class ResponseModel<T> {
    int status;
    String message;
    T data;

    public static <E> ResponseModel<E> ok(E data) {
        return withStatus(Status.OK, data);
    }

    public static <E> ResponseModel<E> withStatus(Status status, E data) {
        return new ResponseModel<E>(status.getCode(), status.getMessage(), data);
    }
}
