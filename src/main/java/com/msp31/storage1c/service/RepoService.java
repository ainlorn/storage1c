package com.msp31.storage1c.service;

import com.msp31.storage1c.domain.dto.request.CreateRepoRequest;
import com.msp31.storage1c.domain.dto.response.RepoAccessLevelInfo;
import com.msp31.storage1c.domain.dto.response.RepoInfo;
import com.msp31.storage1c.domain.dto.response.RepoInfoResponse;
import com.msp31.storage1c.domain.dto.response.RepoUserAccessInfo;

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
}
