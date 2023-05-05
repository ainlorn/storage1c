package com.msp31.storage1c.adapter.web.controller;

import com.msp31.storage1c.adapter.web.annotation.ApiV1;
import com.msp31.storage1c.domain.dto.response.*;
import com.msp31.storage1c.service.RepoService;
import com.msp31.storage1c.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;

import static com.msp31.storage1c.domain.dto.response.ResponseModel.ok;

@ApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;
    RepoService repoService;

    @GetMapping("/me")
    public ResponseModel<UserInfo> getCurrentUserInfo() {
        return ok(userService.getCurrentUserInfo());
    }

    @GetMapping("/me/repos")
    public ResponseModel<List<RepoInfo>> getCurrentUserRepos() {
        return ok(repoService.getReposForCurrentUser());
    }

    @RequestMapping("/users/{username:(?!^\\d+$)^[a-zA-Z0-9_-]+$}/{*path}")
    public void redirect(@PathVariable String username,
                         @PathVariable String path,
                         HttpServletResponse response) throws IOException {
        var userId = userService.getUserIdByUsername(username);
        response.sendRedirect("/api/v1/users/%d%s".formatted(userId, path));
    }

    @GetMapping("/users/{userId:\\d+}")
    public ResponseModel<PublicUserInfo> getUserInfoById(@PathVariable long userId) {
        return ok(userService.getPublicUserInfo(userId));
    }

    @GetMapping("/users/{userId:\\d+}/repos")
    public ResponseModel<List<RepoInfo>> getUserRepos(@PathVariable long userId) {
        return ok(repoService.getReposForUser(userId));
    }
}
