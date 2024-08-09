package com.msp31.storage1c.service.impl;

import com.msp31.storage1c.adapter.repository.*;
import com.msp31.storage1c.common.exception.*;
import com.msp31.storage1c.config.properties.FileLockingProperties;
import com.msp31.storage1c.config.properties.GitProperties;
import com.msp31.storage1c.config.properties.UnpackProperties;
import com.msp31.storage1c.domain.dto.request.*;
import com.msp31.storage1c.domain.dto.response.*;
import com.msp31.storage1c.domain.entity.account.User;
import com.msp31.storage1c.domain.entity.repo.*;
import com.msp31.storage1c.domain.entity.repo.model.*;
import com.msp31.storage1c.domain.mapper.RepoMapper;
import com.msp31.storage1c.domain.mapper.UserMapper;
import com.msp31.storage1c.module.git.Git;
import com.msp31.storage1c.module.git.GitCommit;
import com.msp31.storage1c.module.git.GitFile;
import com.msp31.storage1c.module.git.exception.GitTargetDirectoryIsAFileException;
import com.msp31.storage1c.module.git.exception.GitTargetFileIsADirectoryException;
import com.msp31.storage1c.service.RepoService;
import com.msp31.storage1c.utils.Hex;
import com.msp31.storage1c.utils.PathNormalizer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.eclipse.jgit.util.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service("repoService")
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RepoServiceImpl implements RepoService {
    static final String ownerAccessLevel = "MANAGER";
    static final String viewAccessLevel = "VIEWER";

    GitProperties gitProperties;
    UnpackProperties unpackProperties;
    FileLockingProperties fileLockingProperties;
    UserRepository userRepository;
    UserMapper userMapper;
    RepoRepository repoRepository;
    RepoAccessLevelRepository repoAccessLevelRepository;
    RepoUserAccessRepository repoUserAccessRepository;
    RepoTagRepository repoTagRepository;
    RepoCommitRepository repoCommitRepository;
    RepoCommitTagRepository repoCommitTagRepository;
    RepoFileRepository repoFileRepository;
    RepoFileTagRepository repoFileTagRepository;
    RepoFileLockRepository repoFileLockRepository;
    RepoMapper repoMapper;
    Git git;

    @Override
    @PreAuthorize("isAuthenticated()")
    public RepoInfoResponse createRepo(CreateRepoRequest request) {
        var currentUser = userRepository.getCurrentUser();

        if (repoRepository.findByOwnerAndName(currentUser, request.getRepoName()).isPresent())
            throw new RepositoryNameInUseException();

        var model = repoMapper.createModelFrom(request, currentUser);
        var repo = Repo.createFromModel(model);

        var accessLevel = repoAccessLevelRepository.findByName(ownerAccessLevel);
        var userAccessModel = new RepoUserAccessModel(repo, currentUser, accessLevel);
        repo.addUser(RepoUserAccess.createFromModel(userAccessModel));

        if (request.getTags() != null) {
            for (var tag : request.getTags()) {
                repo.addTag(RepoTag.createFromModel(new RepoTagModel(repo, tag)));
            }
        }

        repo = repoRepository.save(repo);

        try (var gitRepo = git.createRepository(repo.getDirectoryName())) {
            gitRepo.newCommit()
                    .addEmptyFile(".gitkeep")
                    .setAuthor(userMapper.createGitIdentityFrom(currentUser))
                    .setMessage("Проект создан")
                    .commit();
        }

        return repoMapper.createRepoInfoResponseFrom(repo, accessLevel);
    }

    @Override
    @PreAuthorize("@repoService.getAccessLevel(#repoId).canManage")
    public RepoInfo patchRepo(long repoId, PatchRepoRequest request) {
        var repo = repoRepository.getReferenceById(repoId);
        var currentUser = userRepository.getCurrentUser();

        if (request.getIsPrivate() != null) {
            if (!Objects.equals(repo.getOwner().getId(), currentUser.getId()))
                throw new AccessDeniedException(); // only owner can change repository access level

            var newAccessLevel = repoMapper.findAccessLevelBy(request.getIsPrivate());
            repo.setDefaultAccessLevel(newAccessLevel);
        }

        if (request.getTags() != null) {
            repo.getTags().clear();
            for (var tag : request.getTags()) {
                Repo finalRepo = repo;
                repo.addTag(repoTagRepository.findByRepoAndTag(repo, tag)
                        .orElseGet(() -> RepoTag.createFromModel(new RepoTagModel(finalRepo, tag))));
            }
        }

        if (request.getDescription() != null) {
            repo.setDescription(request.getDescription());
        }

        if (request.getRepoName() != null) {
            repo.setName(request.getRepoName());
        }

        repo = repoRepository.save(repo);
        return repoMapper.createRepoInfoFrom(repo);
    }

    @Override
    public long getRepoId(String owner, String repoName) {
        var repo = repoRepository.findByOwnerUsernameAndName(owner, repoName);
        if (repo.isEmpty())
            throw new RepositoryNotFoundException();

        return repo.get().getId();
    }

    @Override
    public RepoAccessLevelInfo getAccessLevel(long repoId) {
        return repoMapper.createRepoAccessLevelInfoFrom(
                getAccessLevelInternal(repoRepository.findById(repoId).orElse(null))
        );
    }

    private RepoAccessLevel getAccessLevelInternal(Repo repo) {
        var user = userRepository.getCurrentUser();

        if (repo == null)
            throw new RepositoryNotFoundException();

        if (user != null) {
            var userAccess = repoUserAccessRepository.findByRepoAndUser(repo, user);
            if (userAccess.isPresent())
                return userAccess.get().getAccessLevel();
        }

        return repo.getDefaultAccessLevel();
    }

    @Override
    @PreAuthorize("@repoService.getAccessLevel(#repoId).canView")
    public RepoInfoResponse getRepoInfo(long repoId) {
        var repo = repoRepository.getReferenceById(repoId);

        return repoMapper.createRepoInfoResponseFrom(repo, getAccessLevelInternal(repo));
    }

    @Override
    public List<RepoInfo> getReposForUser(long userId) {
        var user = userRepository.findById(userId);
        if (user.isEmpty())
            throw new UserNotFoundException();

        var repos = repoRepository.findAllByOwner(user.get());

        return repos.stream()
                .filter(repo -> repo.getDefaultAccessLevel().isCanView())
                .map(repoMapper::createRepoInfoFrom)
                .toList();
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public List<RepoInfo> getReposForCurrentUser() {
        var user = userRepository.getCurrentUser();
        var repos = repoRepository.findAllByUserHasAccess(user.getId());
        return repos.stream()
                .map(repoMapper::createRepoInfoFrom)
                .toList();
    }

    @Override
    @PreAuthorize("@repoService.getAccessLevel(#repoId).canView")
    public List<RepoUserAccessInfo> getUsersForRepo(long repoId) {
        var repo = repoRepository.findById(repoId);
        if (repo.isEmpty())
            throw new RepositoryNotFoundException();

        return repo.get().getUsers().stream()
                .map(repoMapper::createRepoUserAccessInfoFrom)
                .toList();
    }

    @Override
    @PreAuthorize("@repoService.getAccessLevel(#repoId).canManage")
    public void addUserToRepo(long repoId, long userId, String roleName) {
        var repo = repoRepository.getReferenceById(repoId);
        var currentUser = userRepository.getCurrentUser();
        var targetUser = userRepository.findById(userId);
        if (targetUser.isEmpty())
            throw new UserNotFoundException();
        var role = repoAccessLevelRepository.findByName(roleName);
        if (role == null)
            throw new AccessLevelNotFoundException();

        if (repoUserAccessRepository.findByRepoAndUser(repo, targetUser.get()).isPresent())
            throw new UserAlreadyAddedException();

        if (role.isCanManage() && !Objects.equals(currentUser.getId(), repo.getOwner().getId()))
            throw new AccessDeniedException(); // only repo owner can add managers

        var userAccess = new RepoUserAccessModel(repo, targetUser.get(), role);
        repo.addUser(RepoUserAccess.createFromModel(userAccess));
        repoRepository.save(repo);
    }

    @Override
    @PreAuthorize("@repoService.getAccessLevel(#repoId).canManage")
    public void removeUserFromRepo(long repoId, long userId) {
        var repo = repoRepository.getReferenceById(repoId);
        var currentUser = userRepository.getCurrentUser();
        var targetUser = userRepository.findById(userId);
        if (targetUser.isEmpty())
            throw new UserNotFoundException();
        var userAccess = repoUserAccessRepository.findByRepoAndUser(repo, targetUser.get());
        if (userAccess.isEmpty())
            throw new UserNotFoundException();

        if (userAccess.get().getAccessLevel().isCanManage()
                && !Objects.equals(currentUser.getId(), repo.getOwner().getId()))
            throw new AccessDeniedException(); // only repo owner can remove managers

        repo.removeUser(userAccess.get());
        repoRepository.save(repo);
    }

    @Override
    @PreAuthorize("@repoService.getAccessLevel(#request.repoId).canCommit")
    public CommitInfo pushFile(PushFileRequest request) {
        var dbRepo = repoRepository.getReferenceById(request.getRepoId());
        var user = userRepository.getCurrentUser();
        GitCommit gitCommit;

        var normalizedPath = PathNormalizer.normalize(request.getPath());
        var dbExistingFile =
                repoFileRepository.findByRepoAndPath(dbRepo, normalizedPath);

        dbExistingFile.ifPresent(repoFile ->
                ensureLocked(user, repoFile));

        try (var gitRepo = git.openRepository(dbRepo.getDirectoryName())) {
            gitCommit = gitRepo.newCommit()
                    .addFile(request.getPath(), request.getFileStream())
                    .setAuthor(userMapper.createGitIdentityFrom(user))
                    .setMessage(request.getMessage())
                    .commit();
        }

        var dbFile = dbExistingFile.orElseGet(() ->
                RepoFile.createFromModel(new RepoFileModel(dbRepo, normalizedPath, "")));

        if (request.getFileDescription() != null)
            dbFile.setDescription(request.getFileDescription());

        if (request.getFileTags() != null) {
            dbFile.getTags().clear();
            for (var tag : request.getFileTags()) {
                var newTag = RepoFileTag.createFromModel(new RepoFileTagModel(dbFile, tag));
                if (dbFile.getId() == null)
                    dbFile.addTag(newTag);
                else
                    dbFile.addTag(repoFileTagRepository.findByFileAndTag(dbFile, tag).orElse(newTag));
            }
        }
        dbFile = repoFileRepository.save(dbFile);

        var dbCommit = RepoCommit.createFromModel(new RepoCommitModel(dbRepo, gitCommit.getId()));
        if (request.getCommitTags() != null) {
            for (var tag : request.getCommitTags()) {
                var dbTag = RepoCommitTag.createFromModel(new RepoCommitTagModel(dbCommit, tag));
                dbCommit.addTag(dbTag);
            }
        }
        dbCommit = repoCommitRepository.save(dbCommit);

        if (dbExistingFile.isPresent())
            unlockFileInternal(dbFile);

        return repoMapper.createCommitInfoFrom(gitCommit, dbCommit);
    }

    @Override
    @PreAuthorize("@repoService.getAccessLevel(#repoId).canCommit")
    public CommitInfo deleteFile(long repoId, String path) {
        var dbRepo = repoRepository.getReferenceById(repoId);
        var user = userRepository.getCurrentUser();
        CommitInfo commitInfo;

        var dbFile = repoFileRepository.findByRepoAndPath(dbRepo, PathNormalizer.normalize(path));
        dbFile.ifPresent(f ->
                ensureNotLockedByAnotherUser(user, f));

        try (var gitRepo = git.openRepository(dbRepo.getDirectoryName())) {
            var gitCommit = gitRepo.newCommit()
                    .deleteFile(path)
                    .setAuthor(userMapper.createGitIdentityFrom(user))
                    .setMessage("Удалён файл '%s'".formatted(path))
                    .commit();
            commitInfo = repoMapper.createCommitInfoFrom(gitCommit, dbRepo);
        }

        dbFile.ifPresent(repoFileRepository::delete);

        return commitInfo;
    }

    @Override
    @PreAuthorize("@repoService.getAccessLevel(#repoId).canView")
    public FileDownloadInfo prepareFileDownload(long repoId, String path, String rev) {
        var dbRepo = repoRepository.getReferenceById(repoId);
        try (var gitRepo = git.openRepository(dbRepo.getDirectoryName())) {
            var blobId = gitRepo.getBlobIdForFile(path, rev);
            return makeFileDownloadInfo(repoId, blobId, path);
        }
    }

    @Override
    @PreAuthorize("@repoService.getAccessLevel(#repoId).canView")
    public void writeBlobToOutputStream(long repoId, String blobKey, OutputStream outputStream) {
        var dbRepo = repoRepository.getReferenceById(repoId);
        try (var gitRepo = git.openRepository(dbRepo.getDirectoryName())) {
            var blobId = blobKey.substring(0, blobKey.indexOf(':'));
            if (!blobKey.equals(makeBlobKey(repoId, blobId)))
                return;

            gitRepo.writeBlobToOutputStream(blobId, outputStream);
        }
    }

    @Override
    @PreAuthorize("@repoService.getAccessLevel(#repoId).canView")
    public void writeBlobZipToOutputStream(long repoId, String blobKey, OutputStream outputStream) {
        var dbRepo = repoRepository.getReferenceById(repoId);
        try (var gitRepo = git.openRepository(dbRepo.getDirectoryName())) {
            var blobId = blobKey.substring(0, blobKey.indexOf(':'));
            if (!blobKey.equals(makeBlobKey(repoId, blobId)))
                return;

            try {
                var tempName = String.valueOf(System.currentTimeMillis());
                var cfFile = Path.of(unpackProperties.getRoot(), tempName + ".cf").toFile();
                var unpackedDir = Path.of(unpackProperties.getRoot(), tempName).toFile();

                var cfOs = new FileOutputStream(cfFile);
                gitRepo.writeBlobToOutputStream(blobId, cfOs);
                cfOs.close();

                unpackedDir.mkdir();
                var process = new ProcessBuilder(
                        unpackProperties.getV8unpackPath(),
                        "-E",
                        cfFile.getAbsolutePath(),
                        unpackedDir.getAbsolutePath()
                ).start();
                process.waitFor();
                if (process.exitValue() != 0)
                    throw new RuntimeException("v8unpack failed with exit code " + process.exitValue());

                zip(unpackedDir, outputStream);
                cfFile.delete();
                FileUtils.delete(unpackedDir, FileUtils.RECURSIVE);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    @PreAuthorize("@repoService.getAccessLevel(#repoId).canView")
    public FileTreeInfo listFiles(long repoId) {
        var dbRepo = repoRepository.getReferenceById(repoId);
        try (var gitRepo = git.openRepository(dbRepo.getDirectoryName())) {
            return repoMapper.createFileTreeInfoFrom(dbRepo, gitRepo.listFiles());
        }
    }

    @Override
    @PreAuthorize("@repoService.getAccessLevel(#repoId).canView")
    public List<CommitInfo> listCommitsForFile(long repoId, String path) {
        var dbRepo = repoRepository.getReferenceById(repoId);
        try (var gitRepo = git.openRepository(dbRepo.getDirectoryName())) {
            return gitRepo.listCommitsForFile(path)
                    .stream()
                    .map((commit) -> repoMapper.createCommitInfoFrom(commit, dbRepo))
                    .toList();
        }
    }

    @Override
    public List<RepoInfo> getAllPublicRepos() {
        var repos = repoRepository.findAllByDefaultAccessLevel_Name(viewAccessLevel);
        return repos.stream().map(repoMapper::createRepoInfoFrom).toList();
    }

    @Override
    @PreAuthorize("@repoService.getAccessLevel(#repoId).canView")
    public FileInfo getFullFileInfo(long repoId, String path, String rev) {
        var dbRepo = repoRepository.getReferenceById(repoId);
        GitFile gitFile;
        FileDownloadInfo downloadInfo;

        try (var gitRepo = git.openRepository(dbRepo.getDirectoryName())) {
            gitFile = gitRepo.getFileInfo(path, rev);
            downloadInfo = makeFileDownloadInfo(repoId, gitFile.getBlobId(), path);
        }

        var normalizedPath = PathNormalizer.normalize(path);
        var dbFile = repoFileRepository.findByRepoAndPath(dbRepo, normalizedPath).orElse(null);

        return repoMapper.createFileInfoFrom(gitFile, downloadInfo, dbFile);
    }

    @Override
    @PreAuthorize("@repoService.getAccessLevel(#repoId).canView")
    public CommitInfo getFullCommitInfo(long repoId, String commitId) {
        var dbRepo = repoRepository.getReferenceById(repoId);
        GitCommit gitCommit;

        try (var gitRepo = git.openRepository(dbRepo.getDirectoryName())) {
            gitCommit = gitRepo.getCommit(commitId);
        }

        var dbCommit = repoCommitRepository.findByRepoAndCommitId(dbRepo, commitId).orElse(null);

        return repoMapper.createCommitInfoFrom(gitCommit, dbCommit);
    }

    @Override
    @PreAuthorize("@repoService.getAccessLevel(#repoId).canCommit")
    public FileInfo patchFileInfo(long repoId, String path, PatchFileInfoRequest request) {
        var dbRepo = repoRepository.getReferenceById(repoId);
        GitFile gitFile;

        try (var gitRepo = git.openRepository(dbRepo.getDirectoryName())) {
            gitFile = gitRepo.getFileInfo(path, "HEAD");
        }


        var dbFile = createOrFindDbFile(dbRepo, path);

        if (request.getDescription() != null) {
            dbFile.setDescription(request.getDescription());
        }

        if (request.getTags() != null) {
            dbFile.getTags().clear();
            for (var tag : request.getTags()) {
                var newTag = RepoFileTag.createFromModel(new RepoFileTagModel(dbFile, tag));
                if (dbFile.getId() == null)
                    dbFile.addTag(newTag);
                else
                    dbFile.addTag(repoFileTagRepository.findByFileAndTag(dbFile, tag).orElse(newTag));
            }
        }

        dbFile = repoFileRepository.save(dbFile);
        return repoMapper.createFileInfoFrom(gitFile, makeFileDownloadInfo(repoId, gitFile.getBlobId(), path), dbFile);
    }

    @Override
    @PreAuthorize("@repoService.getAccessLevel(#repoId).canCommit")
    public CommitInfo patchCommitInfo(long repoId, String rev, PatchCommitInfoRequest request) {
        var dbRepo = repoRepository.getReferenceById(repoId);
        GitCommit gitCommit;

        try (var gitRepo = git.openRepository(dbRepo.getDirectoryName())) {
            gitCommit = gitRepo.getCommit(rev);
        }

        var dbCommit = repoCommitRepository.findByRepoAndCommitId(dbRepo, gitCommit.getId())
                .orElseGet(() -> RepoCommit.createFromModel(new RepoCommitModel(dbRepo, gitCommit.getId())));

        if (request.getTags() != null) {
            dbCommit.getTags().clear();
            for (var tag : request.getTags()) {
                var newTag = RepoCommitTag.createFromModel(new RepoCommitTagModel(dbCommit, tag));
                if (dbCommit.getId() == null)
                    dbCommit.addTag(newTag);
                else
                    dbCommit.addTag(repoCommitTagRepository.findByCommitAndTag(dbCommit, tag).orElse(newTag));
            }
        }

        dbCommit = repoCommitRepository.save(dbCommit);
        return repoMapper.createCommitInfoFrom(gitCommit, dbCommit);
    }

    @Override
    @PreAuthorize("@repoService.getAccessLevel(#repoId).canCommit")
    public void lockFile(long repoId, String path) {
        if (!fileLockingProperties.isEnabled())
            throw new OperationNotAllowedException();

        var dbRepo = repoRepository.getReferenceById(repoId);
        var user = userRepository.getCurrentUser();

        try (var gitRepo = git.openRepository(dbRepo.getDirectoryName())) {
            var fileInfo = gitRepo.getFileInfo(path, "HEAD");
            if (fileInfo.getType().equals(GitFile.TYPE_DIRECTORY))
                throw new TargetFileIsADirectoryException();
        }

        var dbFile = createOrFindDbFile(dbRepo, path);
        var currentLock = repoFileLockRepository.findByFile(dbFile);
        if (currentLock.isPresent()) {
            if (!Objects.equals(currentLock.get().getUser().getId(), user.getId()))
                throw new FileLockedByAnotherUserException();
            return; // if already locked by current user just ignore
        }
        repoFileLockRepository.save(RepoFileLock.createFromModel(new RepoFileLockModel(dbFile, user)));
    }

    @Override
    @PreAuthorize("@repoService.getAccessLevel(#repoId).canManage")
    public void deleteRepository(long repoId) {
        var dbRepo = repoRepository.getReferenceById(repoId);
        var user = userRepository.getCurrentUser();

        if (!Objects.equals(dbRepo.getOwner().getId(), user.getId()))
            throw new AccessDeniedException(); // only owner can delete

        var gitRepo = git.openRepository(dbRepo.getDirectoryName());
        gitRepo.delete();

        repoRepository.delete(dbRepo);
    }

    @Override
    @PreAuthorize("@repoService.getAccessLevel(#repoId).canCommit")
    public void unlockFile(long repoId, String path) {
        if (!fileLockingProperties.isEnabled())
            throw new OperationNotAllowedException();

        var dbRepo = repoRepository.getReferenceById(repoId);
        var dbFile = repoFileRepository.findByRepoAndPath(dbRepo, PathNormalizer.normalize(path));
        unlockFileInternal(dbFile.orElse(null));
    }

    private void unlockFileInternal(RepoFile file) {
        if (!fileLockingProperties.isEnabled())
            return;

        if (file == null)
            throw new FileNotLockedException();
        var lock = repoFileLockRepository.findByFile(file);
        if (lock.isEmpty())
            throw new FileNotLockedException();

        var user = userRepository.getCurrentUser();

        if (!Objects.equals(lock.get().getUser().getId(), user.getId())
                && !getAccessLevelInternal(file.getRepo()).isCanManage())
            throw new FileLockedByAnotherUserException();

        repoFileLockRepository.delete(lock.get());
    }

    private void ensureLocked(User user, RepoFile file) {
        if (!fileLockingProperties.isEnabled())
            return;

        var lock = repoFileLockRepository.findByFile(file);
        if (lock.isEmpty())
            throw new FileNotLockedException();

        if (!Objects.equals(lock.get().getUser().getId(), user.getId()))
            throw new FileLockedByAnotherUserException();
    }

    private void ensureNotLockedByAnotherUser(User user, RepoFile file) {
        if (!fileLockingProperties.isEnabled())
            return;

        var lock = repoFileLockRepository.findByFile(file);
        if (lock.isEmpty())
            return;

        if (!Objects.equals(lock.get().getUser().getId(), user.getId()))
            throw new FileLockedByAnotherUserException();
    }

    private RepoFile createOrFindDbFile(Repo dbRepo, String path) {
        var normalizedPath = PathNormalizer.normalize(path);
        var dbFile = repoFileRepository.findByRepoAndPath(dbRepo, normalizedPath)
                .orElseGet(() -> RepoFile.createFromModel(new RepoFileModel(dbRepo, normalizedPath, "")));

        if (dbFile.getId() == null)
            return repoFileRepository.save(dbFile);
        return dbFile;
    }


    private String makeBlobKey(long repoId, String blobId) {
        try {
            var keyRaw = "%d_%s_%s".formatted(repoId, blobId, gitProperties.getFileDownloadKey());
            var messageDigest = MessageDigest.getInstance("SHA-256");
            var hashBytes = messageDigest.digest(keyRaw.getBytes(StandardCharsets.UTF_8));
            return blobId + ':' + Hex.bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private String makeBlobDownloadUrl(long repoId, String blobKey, String filePath) {
        return gitProperties.getFileDownloadUrl().formatted(
                repoId,
                blobKey,
                filePath.substring(filePath.lastIndexOf('/') + 1)
        );
    }

    private String makeBlobZipDownloadUrl(long repoId, String blobKey, String filePath) {
        return gitProperties.getZipDownloadUrl().formatted(
                repoId,
                blobKey,
                filePath.substring(filePath.lastIndexOf('/') + 1)
        );
    }

    private FileDownloadInfo makeFileDownloadInfo(long repoId, String blobId, String path) {
        if (blobId == null)
            return null;

        var blobKey = makeBlobKey(repoId, blobId);
        var downloadUrl = makeBlobDownloadUrl(repoId, blobKey, path);

        String zipUrl = null;
        if (path.endsWith(".epf") || path.endsWith(".erf") || path.endsWith(".cf"))
            zipUrl = makeBlobZipDownloadUrl(repoId, blobKey, path);

        return new FileDownloadInfo(downloadUrl, zipUrl);
    }

    public static void zip(File directory, OutputStream out) throws IOException {
        URI base = directory.toURI();
        Deque<File> queue = new LinkedList<File>();
        queue.push(directory);
        Closeable res = out;
        try {
            ZipOutputStream zout = new ZipOutputStream(out);
            res = zout;
            while (!queue.isEmpty()) {
                directory = queue.pop();
                for (File kid : directory.listFiles()) {
                    String name = base.relativize(kid.toURI()).getPath();
                    if (kid.isDirectory()) {
                        queue.push(kid);
                        name = name.endsWith("/") ? name : name + "/";
                        zout.putNextEntry(new ZipEntry(name));
                    } else {
                        zout.putNextEntry(new ZipEntry(name));
                        copy(kid, zout);
                        zout.closeEntry();
                    }
                }
            }
        } finally {
            res.close();
        }
    }
    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        while (true) {
            int readCount = in.read(buffer);
            if (readCount < 0) {
                break;
            }
            out.write(buffer, 0, readCount);
        }
    }

    private static void copy(File file, OutputStream out) throws IOException {
        InputStream in = new FileInputStream(file);
        try {
            copy(in, out);
        } finally {
            in.close();
        }
    }

    private static void copy(InputStream in, File file) throws IOException {
        OutputStream out = new FileOutputStream(file);
        try {
            copy(in, out);
        } finally {
            out.close();
        }
    }
}
