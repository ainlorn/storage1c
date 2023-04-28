package com.msp31.storage1c.adapter.web.controller;

import com.msp31.storage1c.adapter.web.annotation.ApiV1;
import com.msp31.storage1c.domain.dto.request.CreateRepoRequest;
import com.msp31.storage1c.domain.dto.response.RepoInfoResponse;
import com.msp31.storage1c.domain.dto.response.ResponseModel;
import com.msp31.storage1c.service.RepoService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static com.msp31.storage1c.domain.dto.response.ResponseModel.ok;


@ApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RepoController {

    RepoService repoService;

    @PostMapping("/repo/create")
    public ResponseModel<RepoInfoResponse> createRepo(@Valid @RequestBody CreateRepoRequest request) {
        return ok(repoService.createRepo(request));
    }

    @GetMapping("/repo/{id}")
    public ResponseModel<RepoInfoResponse> getRepoInfo(@PathVariable long id) {
        return ok(repoService.getRepoInfo(id));
    }
}
