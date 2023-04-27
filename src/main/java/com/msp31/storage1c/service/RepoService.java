package com.msp31.storage1c.service;

import com.msp31.storage1c.domain.dto.request.CreateRepoRequest;
import com.msp31.storage1c.domain.dto.response.RepoAccessLevelInfo;
import com.msp31.storage1c.domain.dto.response.RepoInfoResponse;
import com.msp31.storage1c.domain.dto.response.RepoUserAccessInfo;

public interface RepoService {
    RepoInfoResponse createRepo(CreateRepoRequest request);
    RepoInfoResponse getRepoInfo(String owner, String name);
    RepoInfoResponse getRepoInfo(long repoId);
}
