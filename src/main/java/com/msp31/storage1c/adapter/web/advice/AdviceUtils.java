package com.msp31.storage1c.adapter.web.advice;

import com.msp31.storage1c.common.constant.Status;
import com.msp31.storage1c.domain.dto.response.ResponseModel;
import org.springframework.http.ResponseEntity;

class AdviceUtils {
    static ResponseEntity<ResponseModel<Object>> createResponse(Status s) {
        return ResponseEntity.status(s.getHttpCode()).body(ResponseModel.withStatus(s, null));
    }
}
