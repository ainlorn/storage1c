package com.msp31.storage1c.common.exception;

import com.msp31.storage1c.common.constant.Status;

public class AccessDeniedException extends BaseException {
    public AccessDeniedException() {
        super(Status.ACCESS_DENIED);
    }
}
