package com.msp31.storage1c.common.exception;

import com.msp31.storage1c.common.constant.Status;

public class FileNotLockedException extends BaseException {
    public FileNotLockedException() {
        super(Status.FILE_NOT_LOCKED);
    }
}
