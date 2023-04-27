package com.msp31.storage1c.common.exception;

import com.msp31.storage1c.common.constant.Status;

public class RepositoryNameInUseException extends BaseException {
    public RepositoryNameInUseException() {
        super(Status.REPOSITORY_NAME_IN_USE);
    }
}
