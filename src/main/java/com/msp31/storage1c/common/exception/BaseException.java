package com.msp31.storage1c.common.exception;

import com.msp31.storage1c.common.constant.Status;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Getter
public abstract class BaseException extends RuntimeException {
    Status status;

    protected BaseException(Status status, String msg) {
        super(msg);
        this.status = status;
    }

    protected BaseException(Status status) {
        super(status.getMessage());
        this.status = status;
    }
}
