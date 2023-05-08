package com.msp31.storage1c.module.git;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.eclipse.jgit.api.Git;

import static com.msp31.storage1c.module.git.GitWrapper.runWrapped;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GitRepository implements AutoCloseable {
    Git git;

    public GitCommitBuilder newCommit() {
        return new GitCommitBuilder(git);
    }

    @Override
    public void close() {
        git.close();
    }
}
