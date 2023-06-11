package com.msp31.storage1c.common.exception;

import com.msp31.storage1c.common.constant.Status;

public class InvalidTagListException extends BaseException {
    public InvalidTagListException() {
        super(Status.INVALID_TAG_LIST);
    }
}
