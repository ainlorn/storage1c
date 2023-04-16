package com.msp31.storage1c.common.exception;

import com.msp31.storage1c.common.constant.Status;

public class UserNotFoundException extends BaseException {
    public UserNotFoundException() {
        super(Status.USER_NOT_FOUND);
    }
}
