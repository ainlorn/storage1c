package com.msp31.storage1c.service.impl;

import com.msp31.storage1c.adapter.repository.*;
import com.msp31.storage1c.common.exception.*;
import com.msp31.storage1c.config.properties.GitProperties;
import com.msp31.storage1c.domain.dto.request.CreateRepoRequest;
import com.msp31.storage1c.domain.dto.request.PatchRepoRequest;
import com.msp31.storage1c.domain.dto.request.PushFileRequest;
import com.msp31.storage1c.domain.dto.response.*;
import com.msp31.storage1c.domain.entity.repo.*;
import com.msp31.storage1c.domain.entity.repo.model.*;
import com.msp31.storage1c.domain.mapper.RepoMapper;
import com.msp31.storage1c.domain.mapper.UserMapper;
import com.msp31.storage1c.module.git.Git;
import com.msp31.storage1c.module.git.GitCommit;
import com.msp31.storage1c.module.git.GitFile;
import com.msp31.storage1c.service.RepoService;
import com.msp31.storage1c.utils.Hex;
import com.msp31.storage1c.utils.PathNormalizer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service("repoService")
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RepoServiceImpl implements RepoService {
    static final String ownerAccessLevel = "MANAGER";
    static final String viewAccessLevel = "VIEWER";

    GitProperties gitProperties;
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
        return repoMapper.createRepoAccessLevelInfoFrom(getAccessLevelInternal(repoId));
    }

    private RepoAccessLevel getAccessLevelInternal(long repoId) {
        var repo = repoRepository.findById(repoId);
        var user = userRepository.getCurrentUser();

        if (repo.isEmpty())
            throw new RepositoryNotFoundException();

        if (user != null) {
            var userAccess = repoUserAccessRepository.findByRepoAndUser(repo.get(), user);
            if (userAccess.isPresent())
                return userAccess.get().getAccessLevel();
        }

        return repo.get().getDefaultAccessLevel();
    }

    @Override
    @PreAuthorize("@repoService.getAccessLevel(#repoId).canView")
    public RepoInfoResponse getRepoInfo(long repoId) {
        var repo = repoRepository.getReferenceById(repoId);

        return repoMapper.createRepoInfoResponseFrom(repo, getAccessLevelInternal(repoId));
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

        try (var gitRepo = git.openRepository(dbRepo.getDirectoryName())) {
            gitCommit = gitRepo.newCommit()
                    .addFile(request.getPath(), request.getFileStream())
                    .setAuthor(userMapper.createGitIdentityFrom(user))
                    .setMessage(request.getMessage())
                    .commit();
        }

        var normalizedPath = PathNormalizer.normalize(request.getPath());
        var dbFile = repoFileRepository.findByRepoAndPath(dbRepo, normalizedPath)
                .orElseGet(() -> RepoFile.createFromModel(new RepoFileModel(dbRepo, normalizedPath, "")));
        if (request.getFileDescription() != null)
            dbFile.setDescription(request.getFileDescription());

        if (request.getFileTags() != null) {
            dbFile.getTags().clear();
            for (var tag : request.getFileTags()) {
                dbFile.addTag(repoFileTagRepository.findByFileAndTag(dbFile, tag)
                        .orElseGet(() -> RepoFileTag.createFromModel(new RepoFileTagModel(dbFile, tag))));
            }
        }
        repoFileRepository.save(dbFile);

        var dbCommit = RepoCommit.createFromModel(new RepoCommitModel(dbRepo, gitCommit.getId()));
        if (request.getCommitTags() != null) {
            for (var tag : request.getCommitTags()) {
                var dbTag = RepoCommitTag.createFromModel(new RepoCommitTagModel(dbCommit, tag));
                dbCommit.addTag(dbTag);
            }
        }
        repoCommitRepository.save(dbCommit);

        return repoMapper.createCommitInfoFrom(gitCommit, dbCommit);
    }

    @Override
    @PreAuthorize("@repoService.getAccessLevel(#repoId).canCommit")
    public CommitInfo deleteFile(long repoId, String path) {
        var dbRepo = repoRepository.getReferenceById(repoId);
        var user = userRepository.getCurrentUser();
        try (var gitRepo = git.openRepository(dbRepo.getDirectoryName())) {
            var gitCommit = gitRepo.newCommit()
                    .deleteFile(path)
                    .setAuthor(userMapper.createGitIdentityFrom(user))
                    .setMessage("Удалён файл '%s'".formatted(path))
                    .commit();
            return repoMapper.createCommitInfoFrom(gitCommit, dbRepo);
        }
    }

    @Override
    @PreAuthorize("@repoService.getAccessLevel(#repoId).canView")
    public FileDownloadInfo prepareFileDownload(long repoId, String path, String rev) {
        var dbRepo = repoRepository.getReferenceById(repoId);
        try (var gitRepo = git.openRepository(dbRepo.getDirectoryName())) {
            var blobId = gitRepo.getBlobIdForFile(path, rev);
            var key = makeBlobKey(repoId, blobId);
            var url = makeBlobDownloadUrl(repoId, key, path);
            return new FileDownloadInfo(url);
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
    public FileTreeInfo listFiles(long repoId) {
        var dbRepo = repoRepository.getReferenceById(repoId);
        try (var gitRepo = git.openRepository(dbRepo.getDirectoryName())) {
            return repoMapper.createFileTreeInfoFrom(gitRepo.listFiles());
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
    public TagListResponse getTagsForRepo(long repoId) {
        var repo = repoRepository.getReferenceById(repoId);
        return repoMapper.createTagListResponseFrom(repo.getTags());
    }

    @Override
    @PreAuthorize("@repoService.getAccessLevel(#repoId).canManage")
    public TagListResponse addTag(long repoId, String tag) {
        var repo = repoRepository.getReferenceById(repoId);

        if (repoTagRepository.findByRepoAndTag(repo, tag).isEmpty()) {
            repo.addTag(RepoTag.createFromModel(new RepoTagModel(repo, tag)));
            repo = repoRepository.save(repo);
        }

        return repoMapper.createTagListResponseFrom(repo.getTags());
    }

    @Override
    @PreAuthorize("@repoService.getAccessLevel(#repoId).canManage")
    public TagListResponse removeTag(long repoId, String tag) {
        var repo = repoRepository.getReferenceById(repoId);

        var tagEntity = repoTagRepository.findByRepoAndTag(repo, tag);
        if (tagEntity.isPresent()) {
            repo.removeTag(tagEntity.get());
            repo = repoRepository.save(repo);
        }

        return repoMapper.createTagListResponseFrom(repo.getTags());
    }

    @Override
    @PreAuthorize("@repoService.getAccessLevel(#repoId).canView")
    public FileInfo getFullFileInfo(long repoId, String path, String rev) {
        var dbRepo = repoRepository.getReferenceById(repoId);
        GitFile gitFile;
        FileDownloadInfo downloadInfo = null;

        try (var gitRepo = git.openRepository(dbRepo.getDirectoryName())) {
            gitFile = gitRepo.getFileInfo(path, rev);

            if (gitFile.getType().equals(GitFile.TYPE_FILE)) {
                var blobKey = makeBlobKey(repoId, gitFile.getBlobId());
                var downloadUrl = makeBlobDownloadUrl(repoId, blobKey, path);
                downloadInfo = new FileDownloadInfo(downloadUrl);
            }
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
}
