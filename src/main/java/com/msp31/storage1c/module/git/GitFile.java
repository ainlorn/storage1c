package com.msp31.storage1c.module.git;

import com.msp31.storage1c.module.git.exception.GitTargetDirectoryIsAFileException;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class GitFile {
    public static final String TYPE_FILE = "file";
    public static final String TYPE_DIRECTORY = "directory";

    private String name;
    private final String type;
    private final Map<String, GitFile> files;
    private GitCommit lastCommit;

    private GitFile(String type, Map<String, GitFile> files, String name, GitCommit lastCommit) {
        this.type = type;
        this.files = files;
        this.name = name;
        this.lastCommit = lastCommit;
    }

    public GitFile findByPath(String path) {
        var pathSegments = path.split("/");
        return findByPath(pathSegments, 0);
    }

    private GitFile findByPath(String[] pathSegments, int i) {
        while (i < pathSegments.length && pathSegments[i].isEmpty())
            i++;
        if (i == pathSegments.length)
            return this;
        if (!type.equals(TYPE_DIRECTORY))
            throw new GitTargetDirectoryIsAFileException(pathSegments[i]);

        var child = files.get(pathSegments[i]);
        if (child == null)
            return null;
        return child.findByPath(pathSegments, i + 1);
    }

    public void updateLastCommit() {
        if (!type.equals(TYPE_DIRECTORY))
            return;

        for (var child : files.values()) {
            child.updateLastCommit();

            if (lastCommit == null
                    || (child.lastCommit != null && child.lastCommit.getWhen().compareTo(lastCommit.getWhen()) > 0)) {
                lastCommit = child.lastCommit;
            }
        }
    }

    public static GitFile newFile(String name, GitCommit lastCommit) {
        return new GitFile(TYPE_FILE, null, name, lastCommit);
    }

    public static GitFile newDirectory(String name, GitCommit lastCommit) {
        return new GitFile(TYPE_DIRECTORY, new HashMap<>(), name, lastCommit);
    }
}
