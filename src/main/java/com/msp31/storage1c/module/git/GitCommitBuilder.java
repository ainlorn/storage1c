package com.msp31.storage1c.module.git;

import com.msp31.storage1c.module.git.exception.GitCommitFailedException;
import com.msp31.storage1c.module.git.exception.GitEmptyCommitException;
import com.msp31.storage1c.module.git.exception.GitIllegalFilePathException;
import com.msp31.storage1c.module.git.exception.GitTargetFileIsADirectoryException;
import lombok.Getter;
import lombok.Value;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.EmptyCommitException;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import static com.msp31.storage1c.module.git.GitWrapper.runWrapped;

public class GitCommitBuilder {
    private final Git git;
    private final List<GitNewFile> newFiles;
    private final List<String> filesToDelete;
    private String message = ".";
    private GitIdentity author;
    private boolean executed = false;

    GitCommitBuilder(Git git) {
        this.git = git;
        this.newFiles = new ArrayList<>();
        this.filesToDelete = new ArrayList<>();
    }

    public GitCommitBuilder addFile(String path, InputStream inputStream) {
        newFiles.add(new GitNewFile(path, inputStream));
        return this;
    }

    public GitCommitBuilder addEmptyFile(String path) {
        newFiles.add(new GitNewFile(path, InputStream.nullInputStream()));
        return this;
    }

    public GitCommitBuilder addDirectory(String path) {
        newFiles.add(new GitNewFile(path + "/.gitkeep", InputStream.nullInputStream()));
        return this;
    }

    public GitCommitBuilder setMessage(String message) {
        this.message = message;
        return this;
    }

    public GitCommitBuilder setAuthor(GitIdentity author) {
        this.author = author;
        return this;
    }

    public GitCommitBuilder deleteFile(String path) {
        this.filesToDelete.add(path);
        return this;
    }

    public GitCommit commit() {
        if (executed)
            throw new GitCommitFailedException("Commit already executed");
        if (author == null)
            throw new GitCommitFailedException("Commit author is null");
        if (newFiles.isEmpty() && filesToDelete.isEmpty())
            throw new GitEmptyCommitException();
        executed = true;

        try {
            return runWrapped(() -> {
                synchronized (git.getRepository()) {
                    addFiles();
                    removeFiles();

                    var ident = author.toPersonIdent();
                    try {
                        var revCommit = git.commit()
                                .setAuthor(ident)
                                .setCommitter(ident)
                                .setMessage(message)
                                .setAllowEmpty(false)
                                .call();

                        return GitCommit.fromRevCommit(revCommit);
                    } catch (EmptyCommitException e) {
                        throw new GitEmptyCommitException();
                    }
                }
            });
        } finally {
            for (var file : newFiles) {
                try {
                    file.getInputStream().close();
                } catch (IOException ignore) { }
            }
        }
    }

    private void addFiles() throws IOException, GitAPIException {
        if (newFiles.isEmpty())
            return;

        var workDir = git.getRepository().getWorkTree().toPath().toRealPath().toString();

        var addCommand = git.add();
        for (var file : newFiles) {
            var relPath = GitUtils.normalizeRelPath(file.getPath());
            var absPath = Path.of(workDir, relPath).toAbsolutePath().normalize();
            if (!absPath.toString().startsWith(workDir + File.separator))
                throw new GitIllegalFilePathException(absPath.toString());
            if (absPath.toFile().isDirectory())
                throw new GitTargetFileIsADirectoryException(absPath.toString());

            var inputStream = file.getInputStream();
            Files.createDirectories(absPath.getParent());
            Files.copy(inputStream, absPath, StandardCopyOption.REPLACE_EXISTING);
            addCommand.addFilepattern(relPath);
        }
        addCommand.call();
    }

    private void removeFiles() throws IOException, GitAPIException {
        if (filesToDelete.isEmpty())
            return;

        var workDir = git.getRepository().getWorkTree().toPath().toRealPath().toString();
        int added = 0;

        var rmCommand = git.rm().setCached(false);
        for (var path : filesToDelete) {
            var relPath = GitUtils.normalizeRelPath(path);
            var absPath = Path.of(workDir, relPath).toAbsolutePath().normalize();
            var file = absPath.toFile();
            if (file.isDirectory())
                throw new GitTargetFileIsADirectoryException(absPath.toString());
            if (!file.exists())
                continue;
            rmCommand.addFilepattern(relPath);
            added++;
        }
        if (added > 0)
            rmCommand.call();
    }

    @Getter
    private static class GitNewFile {
        private final String path;
        private final InputStream inputStream;

        public GitNewFile(String path, InputStream inputStream) {
            this.path = path;
            this.inputStream = inputStream;
        }
    }
}
