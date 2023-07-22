package com.msp31.storage1c.adapter.web.controller;

import com.msp31.storage1c.adapter.web.annotation.ApiV1;
import com.msp31.storage1c.domain.dto.request.RepoSearchRequest;
import com.msp31.storage1c.domain.dto.response.RepoInfo;
import com.msp31.storage1c.domain.dto.response.ResponseModel;
import com.msp31.storage1c.domain.dto.response.SearchResult;
import com.msp31.storage1c.service.SearchService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;
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
    public ResponseModel<SearchResult<RepoInfo>> searchRepos(@Valid @RequestBody RepoSearchRequest request) {
        return ok(searchService.findPublicReposByAllTags(request));
    }
}
