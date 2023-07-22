package com.msp31.storage1c.service;

import com.msp31.storage1c.domain.dto.request.SearchRequest;
import com.msp31.storage1c.domain.dto.response.FileInfoShort;
import com.msp31.storage1c.domain.dto.response.RepoInfo;
import com.msp31.storage1c.domain.dto.response.SearchResult;

public interface SearchService {
    SearchResult<RepoInfo> findPublicReposByAllTags(SearchRequest request);
    SearchResult<FileInfoShort> findFilesInRepoByAllTags(long repoId, SearchRequest request);
}
