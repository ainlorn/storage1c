package com.msp31.storage1c.common.exception;

import com.msp31.storage1c.common.constant.Status;

public class TargetFileIsADirectoryException extends BaseException {
    public TargetFileIsADirectoryException() {
        super(Status.TARGET_FILE_IS_A_DIRECTORY);
    }
}
