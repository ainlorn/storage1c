package com.msp31.storage1c.domain.mapper;

import com.msp31.storage1c.adapter.repository.*;
import com.msp31.storage1c.config.properties.FileLockingProperties;
import com.msp31.storage1c.domain.dto.request.CreateRepoRequest;
import com.msp31.storage1c.domain.dto.response.*;
import com.msp31.storage1c.domain.entity.account.User;
import com.msp31.storage1c.domain.entity.repo.*;
import com.msp31.storage1c.domain.entity.repo.model.RepoModel;
import com.msp31.storage1c.module.git.GitCommit;
import com.msp31.storage1c.module.git.GitFile;
import com.msp31.storage1c.module.git.GitFileTree;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RepoMapper {
    static final String privateAccessLevelName = "NO_ACCESS";
    static final String publicAccessLevelName = "VIEWER";

    UserMapper userMapper;
    UserRepository userRepository;
    RepoAccessLevelRepository repoAccessLevelRepository;
    RepoCommitRepository repoCommitRepository;
    RepoFileLockRepository repoFileLockRepository;
    FileLockingProperties fileLockingProperties;

    public RepoAccessLevel findAccessLevelBy(boolean isPrivate) {
        return repoAccessLevelRepository.findByName(
                isPrivate
                        ? privateAccessLevelName
                        : publicAccessLevelName
        );
    }

    public RepoModel createModelFrom(CreateRepoRequest request, User user) {
        return new RepoModel(
                request.getRepoName(),
                request.getDescription() != null ? request.getDescription() : "",
                UUID.randomUUID().toString(),
                user,
                findAccessLevelBy(request.getIsPrivate())
        );
    }

    public RepoInfo createRepoInfoFrom(Repo repo) {
        return new RepoInfo(
                repo.getId(),
                repo.getName(),
                repo.getDescription(),
                createTagListResponseFrom(repo.getTags()).getTags(),
                userMapper.createPublicUserInfoFrom(repo.getOwner()),
                !repo.getDefaultAccessLevel().getName().equals(privateAccessLevelName),
                repo.getCreatedOn()
        );
    }

    public RepoInfoResponse createRepoInfoResponseFrom(Repo repo, RepoAccessLevel currentUserAccessLevel) {
        return new RepoInfoResponse(createRepoInfoFrom(repo), createRepoAccessLevelInfoFrom(currentUserAccessLevel));
    }

    public RepoUserAccessInfo createRepoUserAccessInfoFrom(RepoUserAccess userAccess) {
        return new RepoUserAccessInfo(
                userMapper.createPublicUserInfoFrom(userAccess.getUser()),
                createRepoAccessLevelInfoFrom(userAccess.getAccessLevel())
        );
    }

    public RepoAccessLevelInfo createRepoAccessLevelInfoFrom(RepoAccessLevel accessLevel) {
        return new RepoAccessLevelInfo(
                accessLevel.getName(),
                accessLevel.isCanView(),
                accessLevel.isCanCommit(),
                accessLevel.isCanManage()
        );
    }

    public TagListResponse createTagListResponseFrom(Set<RepoTag> repoTags) {
        return new TagListResponse(repoTags.stream().map(RepoTag::getTag).sorted().toList());
    }

    public TagListResponse createFileTagListResponseFrom(Set<RepoFileTag> repoFileTags) {
        return new TagListResponse(repoFileTags.stream().map(RepoFileTag::getTag).sorted().toList());
    }

    public TagListResponse createCommitTagListResponseFrom(Set<RepoCommitTag> repoCommitTags) {
        return new TagListResponse(repoCommitTags.stream().map(RepoCommitTag::getTag).sorted().toList());
    }

    public CommitInfo createCommitInfoFrom(GitCommit gitCommit, Repo repo) {
        var repoCommit = repoCommitRepository.findByRepoAndCommitId(repo, gitCommit.getId());
        return createCommitInfoFrom(gitCommit, repoCommit.orElse(null));
    }

    public CommitInfo createCommitInfoFrom(GitCommit gitCommit, RepoCommit repoCommit) {
        var authorIdentity = gitCommit.getAuthor();
        var author = userRepository.findByEmail(authorIdentity.getEmail());
        PublicUserInfo authorInfo = null;
        if (author.isPresent())
            authorInfo = userMapper.createPublicUserInfoFrom(author.get());

        return new CommitInfo(
                gitCommit.getId(),
                gitCommit.getMessage(),
                repoCommit == null
                        ? new ArrayList<>()
                        : repoCommit.getTags().stream().map(RepoCommitTag::getTag).sorted().toList(),
                authorInfo,
                gitCommit.getWhen()
        );
    }

    public FileInfoShort createFileInfoShortFrom(RepoFile file) {
        return new FileInfoShort(file.getPath(), file.getDescription(),
                file.getTags().stream().map(RepoFileTag::getTag).sorted().collect(Collectors.toList()));
    }

    public CommitInfoShort createCommitInfoShortFrom(GitCommit commit) {
        return new CommitInfoShort(commit.getId(), commit.getMessage(), commit.getWhen());
    }

    public FileTreeInfo createFileTreeInfoFrom(Repo dbRepo, GitFileTree gitFileTree) {

        var fileMap = new HashMap<String, FileTreeInfo.File>();
        var rootInfo = createFileTreeInfoFileFrom(gitFileTree.getRoot(), "", fileMap);
        var result = new FileTreeInfo(rootInfo.getFiles());

        if (fileLockingProperties.isEnabled()) {
            var locks = repoFileLockRepository.findAllByFile_Repo(dbRepo);
            for (var lock : locks) {
                var file = fileMap.getOrDefault(lock.getFile().getPath(), null);
                if (file != null) {
                    file.setLocked(true);
                }
            }
        }

        return result;
    }

    private FileTreeInfo.File createFileTreeInfoFileFrom(GitFileTree.File gitFile,
                                                         String prefix,
                                                         Map<String, FileTreeInfo.File> fileMap) {
        List<FileTreeInfo.File> files = null;
        if (gitFile.getType().equals(GitFileTree.File.TYPE_DIRECTORY)) {
            files = new ArrayList<>();
            for (var child : gitFile.getFiles().values()) {
                if (child.getName().startsWith(".git"))
                    continue;

                files.add(createFileTreeInfoFileFrom(
                        child,
                        prefix.isEmpty()
                                ? gitFile.getName()
                                : prefix + "/" + gitFile.getName(),
                        fileMap
                ));
            }
        }

        CommitInfoShort commitInfo = null;
        var lastCommit = gitFile.getLastCommit();
        if (lastCommit != null)
            commitInfo = createCommitInfoShortFrom(lastCommit);

        var result = new FileTreeInfo.File(
                gitFile.getName(),
                gitFile.getType(),
                files,
                commitInfo,
                fileLockingProperties.isEnabled() ? false : null
        );
        fileMap.put(prefix.isEmpty() ? gitFile.getName() : prefix + "/" + gitFile.getName(), result);
        return result;
    }

    public FileInfo createFileInfoFrom(GitFile gitFile, FileDownloadInfo fileDownloadInfo, RepoFile repoFile) {
        String description = "";
        List<String> tags = new ArrayList<>();
        FileInfo.Lock lock = null;

        if (repoFile != null) {
            description = repoFile.getDescription();
            tags = repoFile.getTags()
                    .stream()
                    .map(RepoFileTag::getTag)
                    .sorted().toList();

            if (fileLockingProperties.isEnabled()) {
                var dbLock = repoFileLockRepository.findByFile(repoFile);
                if (dbLock.isPresent())
                    lock = new FileInfo.Lock(true, userMapper.createPublicUserInfoFrom(dbLock.get().getUser()));
                else
                    lock = new FileInfo.Lock(false, null);
            }
        }

        return new FileInfo(
                gitFile.getName(),
                gitFile.getType(),
                description,
                fileDownloadInfo,
                tags,
                lock
        );
    }
}
