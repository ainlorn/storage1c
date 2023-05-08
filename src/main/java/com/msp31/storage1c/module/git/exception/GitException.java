package com.msp31.storage1c.module.git.exception;

public class GitException extends RuntimeException {
    public GitException() {
    }

    public GitException(String message) {
        super(message);
    }

    public GitException(String message, Throwable cause) {
        super(message, cause);
    }

    public GitException(Throwable cause) {
        super(cause);
    }
}
