package com.msp31.storage1c.common.exception;

import com.msp31.storage1c.common.constant.Status;

public class EmailInUseException extends BaseException {
    public EmailInUseException() {
        super(Status.EMAIL_IN_USE);
    }
}
