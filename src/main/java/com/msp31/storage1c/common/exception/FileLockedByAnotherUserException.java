package com.msp31.storage1c.common.exception;

import com.msp31.storage1c.common.constant.Status;

public class FileLockedByAnotherUserException extends BaseException {
    public FileLockedByAnotherUserException() {
        super(Status.FILE_LOCKED_BY_ANOTHER_USER);
    }
}
