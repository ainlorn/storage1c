package com.msp31.storage1c.module.git;

import com.msp31.storage1c.module.git.exception.GitCommitNotFoundException;
import com.msp31.storage1c.module.git.exception.GitFileNotFoundException;
import com.msp31.storage1c.module.git.exception.GitTargetFileIsADirectoryException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.FileUtils;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.msp31.storage1c.module.git.GitWrapper.runWrapped;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GitRepository implements AutoCloseable {
    Git git;

    public GitCommitBuilder newCommit() {
        return new GitCommitBuilder(git);
    }

    private RevCommit findCommitById(String rev) {
        return runWrapped(() -> {
            var repository = git.getRepository();
            var commitId = repository.resolve(rev + "^0");
            if (commitId == null)
                throw new GitCommitNotFoundException("Commit %s not found!".formatted(rev));

            var revWalk = new RevWalk(repository);
            var revCommit = revWalk.parseCommit(commitId);
            revWalk.close();

            return revCommit;
        });
    }

    public GitCommit getCommit(String rev) {
        return runWrapped(() -> {
            var revCommit = findCommitById(rev);
            return GitCommit.fromRevCommit(revCommit);
        });
    }

    public String getBlobIdForFile(String path, String rev) {
        return runWrapped(() -> {
            var relPath = GitUtils.normalizeRelPath(path);
            var repository = git.getRepository();
            var revCommit = findCommitById(rev);

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

    public GitFile getFileInfo(String path, String rev) {
        return runWrapped(() -> {
            var relPath = GitUtils.normalizeRelPath(path);
            String blobId;
            try {
                blobId = getBlobIdForFile(path, rev);
            } catch (GitTargetFileIsADirectoryException e) {
                return new GitFile(relPath, GitFile.TYPE_DIRECTORY, null);
            }
            return new GitFile(relPath, GitFile.TYPE_FILE, blobId);
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

    public GitFileTree listFiles() {
        return runWrapped(() -> {
            var allFiles = new ArrayList<String>();
            var result = new GitFileTree();

            var repository = git.getRepository();
            var commitId = repository.resolve("HEAD^0");
            var revWalk = new RevWalk(repository);
            var revCommit = revWalk.parseCommit(commitId);
            revWalk.close();

            var revTree = revCommit.getTree();
            var treeWalk = new TreeWalk(repository);
            treeWalk.addTree(revTree);
            treeWalk.setRecursive(false);

            while (treeWalk.next()) {
                var path = treeWalk.getPathString();
                var slashIdx = path.lastIndexOf('/');
                var parentPath = path.substring(0, Math.max(0, slashIdx));
                var fileName = path.substring(slashIdx + 1);
                GitFileTree.File file;
                if (treeWalk.isSubtree()) {
                    treeWalk.enterSubtree();
                    file = GitFileTree.File.newDirectory(fileName, null);
                } else {
                    file = GitFileTree.File.newFile(fileName, null);
                    allFiles.add(path);
                }
                result.findByPath(parentPath).getFiles().put(fileName, file);
            }
            treeWalk.close();

            var commits = findLastCommitForFiles(allFiles);
            for (var path : commits.keySet()) {
                result.findByPath(path).setLastCommit(commits.get(path));
            }

            result.updateLastCommit();

            return result;
        });
    }

    public Map<String, GitCommit> findLastCommitForFiles(List<String> paths) {
        return runWrapped(() -> {
            var repository = git.getRepository();
            var head = repository.resolve("HEAD^0");
            var result = new HashMap<String, GitCommit>();
            for (var path : paths) {
                var iterable = git.log().add(head).addPath(path).setMaxCount(1).call();
                for (var revCommit : iterable) {
                    result.put(path, GitCommit.fromRevCommit(revCommit));
                    break;
                }
            }
            return result;
        });
    }

    public GitCommit findLastCommitForFile(String path) {
        return runWrapped(() -> {
            var repository = git.getRepository();
            var head = repository.resolve("HEAD^0");
            var iterable = git.log().add(head).addPath(path).setMaxCount(1).call();
            for (var revCommit : iterable) {
                return GitCommit.fromRevCommit(revCommit);
            }
            return null;
        });
    }

    public List<GitCommit> listCommitsForFile(String path) {
        if (path.startsWith("/"))
            path = path.substring(1);

        String finalPath = path;
        return runWrapped(() -> {
            var repository = git.getRepository();
            var head = repository.resolve("HEAD^0");
            var iterable = git.log().add(head).addPath(finalPath).call();
            var result = new ArrayList<GitCommit>();
            for (RevCommit revCommit : iterable) {
                result.add(GitCommit.fromRevCommit(revCommit));
;           }
            return result;
        });
    }

    public void delete() {
        runWrapped(() -> {
            var path = git.getRepository().getWorkTree();
            close();
            FileUtils.delete(path, FileUtils.RECURSIVE);
        });
    }

    @Override
    public void close() {
        git.getRepository().close();
    }
}
