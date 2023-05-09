package com.msp31.storage1c.module.git.exception;

public class GitCommitNotFoundException extends GitException {
    public GitCommitNotFoundException() {
    }

    public GitCommitNotFoundException(String message) {
        super(message);
    }
}
