package com.msp31.storage1c.service;

import com.msp31.storage1c.domain.dto.request.CreateRepoRequest;
import com.msp31.storage1c.domain.dto.request.PushFileRequest;
import com.msp31.storage1c.domain.dto.response.*;

import java.io.OutputStream;
import java.util.List;

public interface RepoService {
    RepoInfoResponse createRepo(CreateRepoRequest request);
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
    FileTreeInfo listFiles(long repoId);
}
