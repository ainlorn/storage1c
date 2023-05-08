package com.msp31.storage1c.module.git;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.util.FS;

import java.nio.file.Path;

import static com.msp31.storage1c.module.git.GitWrapper.runWrapped;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Git {
    String rootDirectoryPath;

    public GitRepository createRepository(String directory) {
        return runWrapped(() -> {
            var git = org.eclipse.jgit.api.Git
                    .init()
                    .setDirectory(Path.of(rootDirectoryPath, directory).toFile())
                    .setInitialBranch("master")
                    .call();
            RepositoryCache.register(git.getRepository());
            return new GitRepository(git);
        });
    }

    public GitRepository openRepository(String directory) {
        return runWrapped(() -> {
            var file = Path.of(rootDirectoryPath, directory).toFile();
            var fileKey = RepositoryCache.FileKey.lenient(file, FS.DETECTED);
            var repo = RepositoryCache.open(fileKey);
            var gitObj = new org.eclipse.jgit.api.Git(repo);
            return new GitRepository(gitObj);
        });
    }
}
