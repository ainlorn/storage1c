package com.msp31.storage1c.module.git.exception;

public class GitTargetFileIsADirectoryException extends GitException {
    private final String path;

    public GitTargetFileIsADirectoryException(String path) {
        super("Target file is a directory: %s".formatted(path));
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
