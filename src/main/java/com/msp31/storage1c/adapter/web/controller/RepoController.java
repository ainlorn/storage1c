package com.msp31.storage1c.adapter.web.controller;

import com.msp31.storage1c.adapter.web.annotation.ApiV1;
import com.msp31.storage1c.common.constant.Status;
import com.msp31.storage1c.common.validation.constraint.ValidPath;
import com.msp31.storage1c.domain.dto.request.*;
import com.msp31.storage1c.domain.dto.response.*;
import com.msp31.storage1c.service.RepoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import static com.msp31.storage1c.domain.dto.response.ResponseModel.ok;


@ApiV1
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RepoController {

    RepoService repoService;

    /**
     * Получить список публичных репозиториев
     */
    @GetMapping("/repos")
    public ResponseModel<List<RepoInfo>> getPublicRepos() {
        return ok(repoService.getAllPublicRepos());
    }

    /**
     * Создать репозиторий
     */
    @PostMapping("/repos")
    public ResponseModel<RepoInfoResponse> createRepo(@Valid @RequestBody CreateRepoRequest request) {
        return ok(repoService.createRepo(request));
    }

    /**
     * Обновить настройки репозитория
     * (необязательно передавать все поля)
     * @param id id репозитория
     */
    @PatchMapping("/repos/{id}")
    public ResponseModel<RepoInfo> patchRepo(@PathVariable long id,
                                                     @Valid @RequestBody PatchRepoRequest request) {
        return ok(repoService.patchRepo(id, request));
    }

    /**
     * Получить информацию о репозитории
     * @param id id репозитория
     */
    @GetMapping("/repos/{id}")
    public ResponseModel<RepoInfoResponse> getRepoInfo(@PathVariable long id) {
        return ok(repoService.getRepoInfo(id));
    }

    /**
     * Получить список коммитов для файла
     * @param id id репозитория
     * @param path путь к файлу относительно корневой папки репозитория
     */
    @GetMapping("/repos/{id}/commits/{*path}")
    public ResponseModel<List<CommitInfo>> getCommitsForFile(@PathVariable long id,
                                                       @PathVariable @Valid @ValidPath String path) {
        return ok(repoService.listCommitsForFile(id, path));
    }

    /**
     * Получить подробную информацию о коммите
     * @param id id репозитория
     * @param commitId id коммита
     */
    @GetMapping("/repos/{id}/commitInfo/{commitId}")
    public ResponseModel<CommitInfo> getFullCommitInfo(@PathVariable long id, @PathVariable String commitId) {
        return ok(repoService.getFullCommitInfo(id, commitId));
    }

    /**
     * Обновить информацию о коммите
     * (необязательно передавать все поля)
     * @param id id репозитория
     * @param commitId id коммита
     */
    @PatchMapping("/repos/{id}/commitInfo/{commitId}")
    public ResponseModel<CommitInfo> patchFileInfo(@PathVariable long id,
                                                   @PathVariable @Valid String commitId,
                                                   @Valid @RequestBody PatchCommitInfoRequest request) {
        return ok(repoService.patchCommitInfo(id, commitId, request));
    }

    /**
     * Захватить файл
     * (если завхват файлов включен в конфиге)
     * @param id id репозитория
     * @param path путь к файлу относительно корневой папки репозитория
     */
    @PostMapping("/repos/{id}/lock/{*path}")
    public ResponseModel<Object> lockFile(@PathVariable long id,
                                          @PathVariable @Valid @ValidPath String path) {
        repoService.lockFile(id, path);
        return ok(null);
    }

    /**
     * Снять захват с файла
     * (если завхват файлов включен в конфиге)
     * @param id id репозитория
     * @param path путь к файлу относительно корневой папки репозитория
     */
    @DeleteMapping("/repos/{id}/lock/{*path}")
    public ResponseModel<Object> unlockFile(@PathVariable long id,
                                          @PathVariable @Valid @ValidPath String path) {
        repoService.unlockFile(id, path);
        return ok(null);
    }

    /**
     * Загрузить файл в репозиторий и создать коммит.
     * @param id id репозитория
     * @param path путь к файлу относительно корневой папки репозитория
     * @param message сообщение коммита
     * @param fileDescription описание файла (если файл существует и этот параметр передан, то описание перезаписывется) (необязательно)
     * @param fileTags список меток файла, разделяемых ';' (если файл существует и этот параметр передан, то все предыдущие метки удаляются) (необязательно)
     * @param commitTags список меток коммита, разделяемых ';' (необязательно)
     * @param file файл
     */
    @PostMapping(path = "/repos/{id}/files", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseModel<CommitInfo> pushFile(@PathVariable long id,
                                              @RequestPart @Valid @ValidPath String path,
                                              @RequestPart @Valid @Size(max=65536) String message,
                                              @RequestPart(required = false) String fileDescription,
                                              @RequestPart(required = false) String fileTags,
                                              @RequestPart(required = false) String commitTags,
                                              @RequestPart MultipartFile file) throws IOException {
        var request = new PushFileRequest(
                id,
                path,
                message,
                fileDescription,
                fileTags,
                commitTags,
                file.getInputStream());
        return ok(repoService.pushFile(request));
    }

    /**
     * Получить список файлов
     * @param id id репозитория
     */
    @GetMapping(path = "/repos/{id}/files")
    public ResponseModel<FileTreeInfo> listFiles(@PathVariable long id) {
        return ok(repoService.listFiles(id));
    }

    /**
     * Получить подробную информацию о файле (вкл. ссылку на загрузку файла из репозитория)
     * @param id id репозитория
     * @param path путь к файлу относительно корневой папки репозитория
     * @param rev версия файла (id коммита)
     */
    @GetMapping(path = "/repos/{id}/files/{*path}")
    public ResponseModel<FileInfo> requestFileDownload(@PathVariable long id,
                                                               @PathVariable @Valid @ValidPath String path,
                                                               @RequestParam(defaultValue = "HEAD") String rev) {
        return ok(repoService.getFullFileInfo(id, path, rev));
    }

    /**
     * Обновить информацию о файле
     * (необязательно передавать все поля)
     * @param id id репозитория
     * @param path путь к файлу относительно корневой папки репозитория
     */
    @PatchMapping("/repos/{id}/files/{*path}")
    public ResponseModel<FileInfo> patchFileInfo(@PathVariable long id,
                                                 @PathVariable @Valid @ValidPath String path,
                                                 @Valid @RequestBody PatchFileInfoRequest request) {
        return ok(repoService.patchFileInfo(id, path, request));
    }

    /**
     * Удалить файл из репозитория и создать коммит
     * @param id id репозитория
     * @param path путь к файлу относительно корневой папки репозитория
     */
    @DeleteMapping("/repos/{id}/files/{*path}")
    public ResponseModel<CommitInfo> deleteFile(@PathVariable long id,
                                            @PathVariable @Valid @ValidPath String path) {
        return ok(repoService.deleteFile(id, path));
    }

    private ResponseEntity<StreamingResponseBody> buildStreamingResponseEntity(StreamingResponseBody responseBody,
                                                                               String filename) {
        var httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentDisposition(
                ContentDisposition.attachment().filename(filename, StandardCharsets.UTF_8).build());
        return ResponseEntity.ok().headers(httpHeaders).body(responseBody);
    }

    @GetMapping(path = "/repos/{id}/blobs/{key:[0-9a-f]+:[0-9a-f]+}")
    public ResponseEntity<StreamingResponseBody> downloadBlob(@PathVariable long id,
                                                              @PathVariable String key,
                                                              @RequestParam(defaultValue = "blob.bin") String fname) {
        return buildStreamingResponseEntity(
                outputStream -> repoService.writeBlobToOutputStream(id, key, outputStream),
                fname
        );
    }

    @GetMapping(path = "/repos/{id}/cfzip/{key:[0-9a-f]+:[0-9a-f]+}")
    public ResponseEntity<StreamingResponseBody> downloadZip(@PathVariable long id,
                                                              @PathVariable String key,
                                                              @RequestParam(defaultValue = "blob.bin") String fname) {
        return buildStreamingResponseEntity(
                outputStream -> repoService.writeBlobZipToOutputStream(id, key, outputStream),
                fname + ".zip"
        );
    }

    /**
     * Получить список пользователей, имеющих доступ к репозиторию
     * @param id id репозитория
     */
    @GetMapping("/repos/{id}/users")
    public ResponseModel<List<RepoUserAccessInfo>> getRepoUsers(@PathVariable long id) {
        return ok(repoService.getUsersForRepo(id));
    }

    /**
     * Добавить пользователя в репозиторий
     * @param id id репозитория
     */
    @PostMapping("/repos/{id}/users")
    public ResponseEntity<ResponseModel<Object>> addRepoUser(@PathVariable long id,
                                                               @RequestBody @Valid AddUserToRepoRequest request) {
        if (request.getUserId() != null)
            repoService.addUserToRepo(id, request.getUserId(), request.getRole());
        else if (request.getUsername() != null)
            repoService.addUserToRepo(id, request.getUsername(), request.getRole());
        else
            return ResponseEntity
                    .status(Status.VALIDATION_ERROR.getHttpCode())
                    .body(ResponseModel.withStatus(Status.VALIDATION_ERROR, new ValidationErrorResponse(Set.of("id", "username"))));

        return ResponseEntity.ok(ok(repoService.getUsersForRepo(id)));
    }

    /**
     * Удалить пользователя из репозитория
     * @param repoId id репозитория
     * @param userId id пользователя
     */
    @DeleteMapping("/repos/{repoId}/users/{userId}")
    public ResponseModel<List<RepoUserAccessInfo>> deleteRepoUser(@PathVariable long repoId,
                                                                  @PathVariable long userId) {
        repoService.removeUserFromRepo(repoId, userId);
        return ok(repoService.getUsersForRepo(repoId));
    }

    /**
     * Удалить репозиторий
     * @param repoId id репозитория
     */
    @DeleteMapping("/repos/{repoId}")
    public ResponseModel<Object> deleteRepo(@PathVariable long repoId) {
        repoService.deleteRepository(repoId);
        return ok(null);
    }
}
