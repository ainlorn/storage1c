package com.msp31.storage1c.service.impl;

import com.msp31.storage1c.adapter.repository.RepoFileRepository;
import com.msp31.storage1c.adapter.repository.RepoRepository;
import com.msp31.storage1c.domain.dto.request.SearchRequest;
import com.msp31.storage1c.domain.dto.response.FileInfoShort;
import com.msp31.storage1c.domain.dto.response.RepoInfo;
import com.msp31.storage1c.domain.dto.response.SearchResult;
import com.msp31.storage1c.domain.mapper.RepoMapper;
import com.msp31.storage1c.service.RepoService;
import com.msp31.storage1c.service.SearchService;
import com.msp31.storage1c.utils.TagUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    RepoService repoService;
    RepoRepository repoRepository;
    RepoFileRepository repoFileRepository;
    RepoMapper repoMapper;

    @Override
    public SearchResult<RepoInfo> findPublicReposByAllTags(SearchRequest request) {
        var tagsNormalized = TagUtils.normalizeForSearch(request.getTags());
        var results = repoRepository.findPublicReposByAllTags(tagsNormalized, tagsNormalized.size())
                .stream().map(repoMapper::createRepoInfoFrom).toList();
        return new SearchResult<>(results);
    }

    @Override
    @PreAuthorize("@repoService.getAccessLevel(#repoId).canView")
    public SearchResult<FileInfoShort> findFilesInRepoByAllTags(long repoId, SearchRequest request) {
        var tagsNormalized = TagUtils.normalizeForSearch(request.getTags());
        var results =
                repoFileRepository.findFilesInRepoByAllTags(repoId, tagsNormalized, tagsNormalized.size())
                        .stream().map(repoMapper::createFileInfoShortFrom).toList();
        return new SearchResult<>(results);
    }
}
