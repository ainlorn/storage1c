package com.msp31.storage1c.module.git.exception;

public class GitIllegalFilePathException extends GitException {
    private final String path;

    public GitIllegalFilePathException(String path) {
        super("Illegal file path: %s".formatted(path));
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
