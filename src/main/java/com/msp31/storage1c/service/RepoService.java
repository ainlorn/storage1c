package com.msp31.storage1c.service;

import com.msp31.storage1c.domain.dto.request.*;
import com.msp31.storage1c.domain.dto.response.*;

import java.io.OutputStream;
import java.util.List;

public interface RepoService {
    RepoInfoResponse createRepo(CreateRepoRequest request);
    RepoInfo patchRepo(long repoId, PatchRepoRequest request);
    RepoAccessLevelInfo getAccessLevel(long repoId);
    RepoInfoResponse getRepoInfo(long repoId);
    long getRepoId(String owner, String repoName);
    List<RepoInfo> getReposForUser(long userId);
    List<RepoInfo> getReposForCurrentUser();
    List<RepoUserAccessInfo> getUsersForRepo(long repoId);
    void addUserToRepo(long repoId, long userId, String role);
    void removeUserFromRepo(long repoId, long userId);
    CommitInfo pushFile(PushFileRequest request);
    CommitInfo deleteFile(long repoId, String path);
    FileDownloadInfo prepareFileDownload(long repoId, String path, String rev);
    void writeBlobToOutputStream(long repoId, String blobKey, OutputStream outputStream);
    void writeBlobZipToOutputStream(long repoId, String blobKey, OutputStream outputStream);
    FileTreeInfo listFiles(long repoId);
    List<CommitInfo> listCommitsForFile(long repoId, String path);
    List<RepoInfo> getAllPublicRepos();
    FileInfo getFullFileInfo(long repoId, String path, String rev);
    CommitInfo getFullCommitInfo(long repoId, String commitId);
    FileInfo patchFileInfo(long repoId, String path, PatchFileInfoRequest request);
    CommitInfo patchCommitInfo(long repoId, String rev, PatchCommitInfoRequest request);
    void lockFile(long repoId, String path);
    void unlockFile(long repoId, String path);
    void deleteRepository(long repoId);
}
