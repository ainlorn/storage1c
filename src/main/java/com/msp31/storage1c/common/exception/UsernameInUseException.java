package com.msp31.storage1c.common.exception;

import com.msp31.storage1c.common.constant.Status;

public class UsernameInUseException extends BaseException {
    public UsernameInUseException() {
        super(Status.USERNAME_IN_USE);
    }
}
