package com.msp31.storage1c.module.git;

import com.msp31.storage1c.module.git.exception.WrappedGitException;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;

class GitWrapper {
    static <T> T runWrapped(GitFunc<T> func) {
        try {
            return func.run();
        } catch (IOException e) {
            throw new WrappedGitException(e);
        } catch (GitAPIException e) {
            throw new WrappedGitException(e);
        }
    }

    static void runWrapped(GitRunnable runnable) {
        try {
            runnable.run();
        } catch (IOException e) {
            throw new WrappedGitException(e);
        } catch (GitAPIException e) {
            throw new WrappedGitException(e);
        }
    }

    @FunctionalInterface
    interface GitFunc<T> {
        T run() throws IOException, GitAPIException;
    }

    @FunctionalInterface
    interface GitRunnable {
        void run() throws IOException, GitAPIException;
    }
}
