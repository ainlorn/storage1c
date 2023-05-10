package com.msp31.storage1c.module.git;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class GitFileTree {
    private final GitFile root;

    public GitFileTree() {
        this.root = GitFile.newDirectory("", null);
    }

    public GitFile findByPath(String path) {
        return root.findByPath(path);
    }

    public void updateLastCommit() {
        root.updateLastCommit();
    }
}
