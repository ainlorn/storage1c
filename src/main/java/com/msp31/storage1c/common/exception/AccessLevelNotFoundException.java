package com.msp31.storage1c.common.exception;

import com.msp31.storage1c.common.constant.Status;

public class AccessLevelNotFoundException extends BaseException {
    public AccessLevelNotFoundException() {
        super(Status.ACCESS_LEVEL_NOT_FOUND);
    }
}
