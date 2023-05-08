package com.msp31.storage1c.module.git.exception;

import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;

public class WrappedGitException extends GitException {
    public WrappedGitException(IOException e) {
        super(e);
    }

    public WrappedGitException(GitAPIException e) {
        super(e);
    }
}
