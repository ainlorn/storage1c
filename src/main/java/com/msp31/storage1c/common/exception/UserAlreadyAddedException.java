package com.msp31.storage1c.common.exception;

import com.msp31.storage1c.common.constant.Status;

public class UserAlreadyAddedException extends BaseException {
    public UserAlreadyAddedException() {
        super(Status.USER_ALREADY_ADDED);
    }
}
