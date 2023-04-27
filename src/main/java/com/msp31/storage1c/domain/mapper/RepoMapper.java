package com.msp31.storage1c.domain.mapper;

import com.msp31.storage1c.adapter.repository.RepoAccessLevelRepository;
import com.msp31.storage1c.domain.dto.request.CreateRepoRequest;
import com.msp31.storage1c.domain.dto.response.RepoAccessLevelInfo;
import com.msp31.storage1c.domain.dto.response.RepoInfo;
import com.msp31.storage1c.domain.dto.response.RepoInfoResponse;
import com.msp31.storage1c.domain.dto.response.RepoUserAccessInfo;
import com.msp31.storage1c.domain.entity.account.User;
import com.msp31.storage1c.domain.entity.repo.Repo;
import com.msp31.storage1c.domain.entity.repo.RepoAccessLevel;
import com.msp31.storage1c.domain.entity.repo.RepoUserAccess;
import com.msp31.storage1c.domain.entity.repo.model.RepoModel;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RepoMapper {
    static final String privateAccessLevelName = "NO_ACCESS";
    static final String publicAccessLevelName = "VIEWER";

    UserMapper userMapper;
    RepoAccessLevelRepository repoAccessLevelRepository;

    public RepoModel createModelFrom(CreateRepoRequest request, User user) {
        return new RepoModel(
                request.getRepoName(),
                UUID.randomUUID().toString(),
                user,
                repoAccessLevelRepository.findByName(
                        request.getIsPrivate()
                        ? privateAccessLevelName
                        : publicAccessLevelName
                )
        );
    }

    public RepoInfo createRepoInfoFrom(Repo repo) {
        return new RepoInfo(
                repo.getId(),
                repo.getName(),
                userMapper.createPublicUserInfoFrom(repo.getOwner()),
                !repo.getDefaultAccessLevel().getName().equals(privateAccessLevelName),
                repo.getCreatedOn(),
                repo.getUsers().stream().map(this::createRepoUserAccessInfoFrom).toList()
        );
    }

    public RepoInfoResponse createRepoInfoResponseFrom(Repo repo, RepoAccessLevel currentUserAccessLevel) {
        return new RepoInfoResponse(createRepoInfoFrom(repo), createRepoAccessLevelInfoFrom(currentUserAccessLevel));
    }

    public RepoUserAccessInfo createRepoUserAccessInfoFrom(RepoUserAccess userAccess) {
        return new RepoUserAccessInfo(
                userMapper.createPublicUserInfoFrom(userAccess.getUser()),
                createRepoAccessLevelInfoFrom(userAccess.getAccessLevel())
        );
    }

    public RepoAccessLevelInfo createRepoAccessLevelInfoFrom(RepoAccessLevel accessLevel) {
        return new RepoAccessLevelInfo(
                accessLevel.getName(),
                accessLevel.isCanView(),
                accessLevel.isCanCommit(),
                accessLevel.isCanManage()
        );
    }
}
