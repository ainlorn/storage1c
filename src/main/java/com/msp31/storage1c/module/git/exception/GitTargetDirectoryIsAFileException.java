package com.msp31.storage1c.module.git.exception;

public class GitTargetDirectoryIsAFileException extends GitException {
    private final String path;

    public GitTargetDirectoryIsAFileException(String path) {
        super("Target directory is a file: %s".formatted(path));
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
