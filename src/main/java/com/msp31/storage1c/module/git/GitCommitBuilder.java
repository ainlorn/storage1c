package com.msp31.storage1c.module.git;

import com.msp31.storage1c.module.git.exception.GitCommitFailedException;
import com.msp31.storage1c.module.git.exception.GitIllegalFilePathException;
import com.msp31.storage1c.module.git.exception.GitTargetFileIsADirectoryException;
import lombok.Getter;
import lombok.Value;
import org.eclipse.jgit.api.Git;

import java.io.File;
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
    private String message = ".";
    private GitIdentity author;
    private boolean executed = false;

    GitCommitBuilder(Git git) {
        this.git = git;
        this.newFiles = new ArrayList<>();
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

    public GitCommit commit() {
        if (executed)
            throw new GitCommitFailedException("Commit already executed");
        if (author == null)
            throw new GitCommitFailedException("Commit author is null");
        if (newFiles.isEmpty())
            throw new GitCommitFailedException("Commit contains no files");
        executed = true;

        return runWrapped(() -> {
            synchronized (git.getRepository()) {
                var workDir = git.getRepository().getWorkTree().toPath().toRealPath().toString();
                var addCommand = git.add();
                for (var file : newFiles) {
                    var relPath = Path.of(file.getPath()).normalize().toString();
                    var absPath = Path.of(workDir, relPath).toAbsolutePath().normalize();
                    if (!absPath.toString().startsWith(workDir + File.separator))
                        throw new GitIllegalFilePathException(absPath.toString());
                    if (absPath.toFile().isDirectory())
                        throw new GitTargetFileIsADirectoryException(absPath.toString());

                    var inputStream = file.getInputStream();
                    Files.createDirectories(absPath.getParent());
                    Files.copy(inputStream, absPath, StandardCopyOption.REPLACE_EXISTING);
                    inputStream.close();
                    addCommand.addFilepattern(relPath);
                }
                addCommand.call();
                var ident = author.toPersonIdent();
                var revCommit = git.commit()
                        .setAuthor(ident)
                        .setCommitter(ident)
                        .setMessage(message)
                        .call();

                return GitCommit.fromRevCommit(revCommit);
            }
        });
    }

    @Getter
    private static class GitNewFile {
        private final String path;
        private final InputStream inputStream;

        public GitNewFile(String path, InputStream inputStream) {
            if (!path.startsWith(File.separator))
                path = File.separator + path;
            this.path = path;
            this.inputStream = inputStream;
        }
    }
}