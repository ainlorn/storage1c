package com.msp31.storage1c.adapter.web.advice;

import com.msp31.storage1c.adapter.web.annotation.ApiAdvice;
import com.msp31.storage1c.common.constant.Status;
import com.msp31.storage1c.common.exception.BaseException;
import com.msp31.storage1c.domain.dto.response.ResponseModel;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ApiAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class DefaultAdvice {

    @ExceptionHandler({BaseException.class})
    public ResponseEntity<ResponseModel<Object>> baseException(BaseException e) {
        return ResponseEntity
                .status(e.getStatus().getHttpCode())
                .body(ResponseModel.withStatus(e.getStatus(), null));
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ResponseModel<Object>> runtimeException(Exception e) {
        var status = Status.UNKNOWN;
        return ResponseEntity
                .status(status.getHttpCode())
                .body(ResponseModel.withStatus(status, null));
    }
}
