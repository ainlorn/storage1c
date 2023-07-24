package com.msp31.storage1c.service.impl;

import com.msp31.storage1c.adapter.repository.RepoCommitRepository;
import com.msp31.storage1c.adapter.repository.RepoFileRepository;
import com.msp31.storage1c.adapter.repository.RepoRepository;
import com.msp31.storage1c.domain.dto.request.SearchRequest;
import com.msp31.storage1c.domain.dto.response.CommitInfo;
import com.msp31.storage1c.domain.dto.response.FileInfoShort;
import com.msp31.storage1c.domain.dto.response.RepoInfo;
import com.msp31.storage1c.domain.dto.response.SearchResult;
import com.msp31.storage1c.domain.mapper.RepoMapper;
import com.msp31.storage1c.module.git.Git;
import com.msp31.storage1c.service.RepoService;
import com.msp31.storage1c.service.SearchService;
import com.msp31.storage1c.utils.TagUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    RepoService repoService;
    RepoRepository repoRepository;
    RepoFileRepository repoFileRepository;
    RepoCommitRepository repoCommitRepository;
    RepoMapper repoMapper;
    Git git;

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

    @Override
    @PreAuthorize("@repoService.getAccessLevel(#repoId).canView")
    public SearchResult<CommitInfo> findCommitsInRepoByAllTags(long repoId, SearchRequest request) {
        var dbRepo = repoRepository.getReferenceById(repoId);

        var tagsNormalized = TagUtils.normalizeForSearch(request.getTags());
        var dbResults =
                repoCommitRepository.findCommitsInRepoByAllTags(repoId, tagsNormalized, tagsNormalized.size());
        var results = new ArrayList<CommitInfo>();

        try (var gitRepo = git.openRepository(dbRepo.getDirectoryName())) {
            for (var dbCommit : dbResults) {
                results.add(repoMapper.createCommitInfoFrom(gitRepo.getCommit(dbCommit.getCommitId()), dbCommit));
            }
        }

        return new SearchResult<>(results);
    }
}
