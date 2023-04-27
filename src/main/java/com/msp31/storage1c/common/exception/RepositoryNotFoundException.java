package com.msp31.storage1c.common.exception;

import com.msp31.storage1c.common.constant.Status;

public class RepositoryNotFoundException extends BaseException {
    public RepositoryNotFoundException() {
        super(Status.REPOSITORY_NOT_FOUND);
    }
}
