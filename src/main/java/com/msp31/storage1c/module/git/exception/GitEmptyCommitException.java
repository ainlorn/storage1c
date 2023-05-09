package com.msp31.storage1c.module.git.exception;

public class GitEmptyCommitException extends GitException {
    public GitEmptyCommitException() {
    }

    public GitEmptyCommitException(String message) {
        super(message);
    }
}
