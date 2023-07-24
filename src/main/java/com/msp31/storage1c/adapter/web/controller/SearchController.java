package com.msp31.storage1c.adapter.web.controller;

import com.msp31.storage1c.adapter.web.annotation.ApiV1;
import com.msp31.storage1c.domain.dto.request.SearchRequest;
import com.msp31.storage1c.domain.dto.response.*;
import com.msp31.storage1c.service.SearchService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static com.msp31.storage1c.domain.dto.response.ResponseModel.ok;

@ApiV1
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SearchController {
    SearchService searchService;

    /**
     * Выполнить поиск репозиториев по всем заданным меткам
     */
    @PostMapping("/search/repos")
    public ResponseModel<SearchResult<RepoInfo>> searchRepos(@Valid @RequestBody SearchRequest request) {
        return ok(searchService.findPublicReposByAllTags(request));
    }

    /**
     * Выполнить поиск файлов в репозитории по всем заданным меткам
     */
    @PostMapping("/repos/{id}/search/files")
    public ResponseModel<SearchResult<FileInfoShort>> searchFilesInRepo(@PathVariable long id,
                                                                        @Valid @RequestBody SearchRequest request) {
        return ok(searchService.findFilesInRepoByAllTags(id, request));
    }

    /**
     * Выполнить поиск коммитов в репозитории по всем заданным меткам
     */
    @PostMapping("/repos/{id}/search/commits")
    public ResponseModel<SearchResult<CommitInfo>> searchCommitsInRepo(@PathVariable long id,
                                                                       @Valid @RequestBody SearchRequest request) {
        return ok(searchService.findCommitsInRepoByAllTags(id, request));
    }
}
