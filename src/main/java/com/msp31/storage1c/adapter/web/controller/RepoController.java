package com.msp31.storage1c.adapter.web.controller;

import com.msp31.storage1c.adapter.web.annotation.ApiV1;
import com.msp31.storage1c.common.validation.constraint.ValidPath;
import com.msp31.storage1c.domain.dto.request.AddUserToRepoRequest;
import com.msp31.storage1c.domain.dto.request.CreateRepoRequest;
import com.msp31.storage1c.domain.dto.request.PushFileRequest;
import com.msp31.storage1c.domain.dto.response.*;
import com.msp31.storage1c.service.RepoService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.util.List;

import static com.msp31.storage1c.domain.dto.response.ResponseModel.ok;


@ApiV1
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RepoController {

    RepoService repoService;

    @PostMapping("/repos")
    public ResponseModel<RepoInfoResponse> createRepo(@Valid @RequestBody CreateRepoRequest request) {
        return ok(repoService.createRepo(request));
    }

    @GetMapping("/repos/{id}")
    public ResponseModel<RepoInfoResponse> getRepoInfo(@PathVariable long id) {
        return ok(repoService.getRepoInfo(id));
    }

    @PostMapping(path = "/repos/{id}/files", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseModel<CommitInfo> pushFile(@PathVariable long id,
                                              @RequestPart @Valid @ValidPath String path,
                                              @RequestPart String message,
                                              @RequestPart MultipartFile file) throws IOException {
        var request = new PushFileRequest(id, path, message, file.getInputStream());
        return ok(repoService.pushFile(request));
    }

    @GetMapping(path = "/repos/{id}/files/{*path}")
    public ResponseModel<FileDownloadInfo> requestFileDownload(@PathVariable long id,
                                                               @PathVariable @Valid @ValidPath String path,
                                                               @RequestParam(defaultValue = "HEAD") String rev) {
        return ok(repoService.prepareFileDownload(id, path, rev));
    }

    @GetMapping(path = "/repos/{id}/blobs/{key:[0-9a-f]+:[0-9a-f]+}")
    public ResponseEntity<StreamingResponseBody> downloadBlob(@PathVariable long id,
                                                              @PathVariable String key,
                                                              @RequestParam(defaultValue = "blob.bin") String fname) {
        StreamingResponseBody responseBody = outputStream -> repoService.writeBlobToOutputStream(id, key, outputStream);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fname)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(responseBody);
    }

    @GetMapping("/repos/{id}/users")
    public ResponseModel<List<RepoUserAccessInfo>> getRepoUsers(@PathVariable long id) {
        return ok(repoService.getUsersForRepo(id));
    }

    @PostMapping("/repos/{id}/users")
    public ResponseModel<List<RepoUserAccessInfo>> addRepoUser(@PathVariable long id,
                                                               @RequestBody @Valid AddUserToRepoRequest request) {
        repoService.addUserToRepo(id, request.getUserId(), request.getRole());
        return ok(repoService.getUsersForRepo(id));
    }

    @DeleteMapping("/repos/{repoId}/users/{userId}")
    public ResponseModel<List<RepoUserAccessInfo>> deleteRepoUser(@PathVariable long repoId,
                                                                  @PathVariable long userId) {
        repoService.removeUserFromRepo(repoId, userId);
        return ok(repoService.getUsersForRepo(repoId));
    }
}
