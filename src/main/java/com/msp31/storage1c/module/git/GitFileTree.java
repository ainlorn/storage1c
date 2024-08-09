package com.msp31.storage1c.module.git;

import com.msp31.storage1c.module.git.exception.GitTargetDirectoryIsAFileException;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class GitFileTree {
    private final GitFileTree.File root;

    public GitFileTree() {
        this.root = GitFileTree.File.newDirectory("", null);
    }

    public GitFileTree.File findByPath(String path) {
        return root.findByPath(path);
    }

    public void updateLastCommit() {
        root.updateLastCommit();
    }

    @Getter
    @Setter
    public static class File {
        public static final String TYPE_FILE = "file";
        public static final String TYPE_DIRECTORY = "directory";

        private String name;
        private final String type;
        private final Map<String, GitFileTree.File> files;
        private GitCommit lastCommit;

        private File(String type, Map<String, GitFileTree.File> files, String name, GitCommit lastCommit) {
            this.type = type;
            this.files = files;
            this.name = name;
            this.lastCommit = lastCommit;
        }

        public GitFileTree.File findByPath(String path) {
            var pathSegments = path.split("/");
            return findByPath(pathSegments, 0);
        }

        private GitFileTree.File findByPath(String[] pathSegments, int i) {
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

        public static GitFileTree.File newFile(String name, GitCommit lastCommit) {
            return new GitFileTree.File(TYPE_FILE, null, name, lastCommit);
        }

        public static GitFileTree.File newDirectory(String name, GitCommit lastCommit) {
            return new GitFileTree.File(TYPE_DIRECTORY, new HashMap<>(), name, lastCommit);
        }
    }
}
