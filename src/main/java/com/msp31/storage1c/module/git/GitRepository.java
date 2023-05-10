package com.msp31.storage1c.module.git;

import com.msp31.storage1c.module.git.exception.GitCommitNotFoundException;
import com.msp31.storage1c.module.git.exception.GitFileNotFoundException;
import com.msp31.storage1c.module.git.exception.GitTargetFileIsADirectoryException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.OutputStream;

import static com.msp31.storage1c.module.git.GitWrapper.runWrapped;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GitRepository implements AutoCloseable {
    Git git;

    public GitCommitBuilder newCommit() {
        return new GitCommitBuilder(git);
    }

    public String getBlobIdForFile(String path, String rev) {
        return runWrapped(() -> {
            var relPath = GitUtils.normalizeRelPath(path);
            var repository = git.getRepository();
            var commitId = repository.resolve(rev + "^0");
            if (commitId == null)
                throw new GitCommitNotFoundException("Commit %s not found!".formatted(rev));

            var revWalk = new RevWalk(repository);
            var revCommit = revWalk.parseCommit(commitId);
            revWalk.close();

            var revTree = revCommit.getTree();
            var treeWalk = TreeWalk.forPath(repository, relPath, revTree);
            if (treeWalk == null)
                throw new GitFileNotFoundException("File %s not found!".formatted(relPath));
            if (treeWalk.isSubtree())
                throw new GitTargetFileIsADirectoryException("File %s is a directory!".formatted(relPath));

            var blobId = treeWalk.getObjectId(0);
            return blobId.getName();
        });
    }

    public void writeBlobToOutputStream(String blobId, OutputStream outputStream) {
        runWrapped(() -> {
            var repository = git.getRepository();
            var blob = repository.resolve(blobId + "^{blob}");
            if (blob == null)
                return;

            try (var objectReader = repository.newObjectReader()) {
                var objectLoader = objectReader.open(blob);
                objectLoader.copyTo(outputStream);
            }
        });
    }

    @Override
    public void close() {
        git.getRepository().close();
    }
}
