package com.msp31.storage1c.common.exception;

import com.msp31.storage1c.common.constant.Status;

public class OperationNotAllowedException extends BaseException {
    public OperationNotAllowedException() {
        super(Status.OPERATION_NOT_ALLOWED);
    }
}
