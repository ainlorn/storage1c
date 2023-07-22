package com.msp31.storage1c.service;

import com.msp31.storage1c.domain.dto.request.RepoSearchRequest;
import com.msp31.storage1c.domain.dto.response.RepoInfo;
import com.msp31.storage1c.domain.dto.response.SearchResult;

public interface SearchService {
    SearchResult<RepoInfo> findPublicReposByAllTags(RepoSearchRequest request);
}
