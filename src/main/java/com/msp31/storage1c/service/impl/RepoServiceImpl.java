package com.msp31.storage1c.service.impl;

import com.msp31.storage1c.adapter.repository.RepoAccessLevelRepository;
import com.msp31.storage1c.adapter.repository.RepoRepository;
import com.msp31.storage1c.adapter.repository.RepoUserAccessRepository;
import com.msp31.storage1c.adapter.repository.UserRepository;
import com.msp31.storage1c.common.exception.RepositoryNameInUseException;
import com.msp31.storage1c.domain.dto.request.CreateRepoRequest;
import com.msp31.storage1c.domain.dto.response.RepoInfoResponse;
import com.msp31.storage1c.domain.entity.repo.Repo;
import com.msp31.storage1c.domain.entity.repo.RepoUserAccess;
import com.msp31.storage1c.domain.entity.repo.model.RepoUserAccessModel;
import com.msp31.storage1c.domain.mapper.RepoMapper;
import com.msp31.storage1c.service.RepoService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RepoServiceImpl implements RepoService {
    static final String ownerAccessLevel = "MANAGER";

    UserRepository userRepository;
    RepoRepository repoRepository;
    RepoAccessLevelRepository repoAccessLevelRepository;
    RepoUserAccessRepository repoUserAccessRepository;
    RepoMapper repoMapper;

    @Override
    @PreAuthorize("isAuthenticated()")
    public RepoInfoResponse createRepo(CreateRepoRequest request) {
        var currentUser = userRepository.getCurrentUser();

        if (repoRepository.findByOwnerAndName(currentUser, request.getRepoName()).isPresent())
            throw new RepositoryNameInUseException();

        var model = repoMapper.createModelFrom(request, currentUser);
        var repo = Repo.createFromModel(model);

        var accessLevel = repoAccessLevelRepository.findByName(ownerAccessLevel);
        var userAccessModel = new RepoUserAccessModel(repo, currentUser, accessLevel);
        repo.addUser(RepoUserAccess.createFromModel(userAccessModel));
        repo = repoRepository.save(repo);

        return repoMapper.createRepoInfoResponseFrom(repo, accessLevel);
    }

    @Override
    public RepoInfoResponse getRepoInfo(String owner, String name) {
        return null;
    }

    @Override
    public RepoInfoResponse getRepoInfo(long repoId) {
        return null;
    }

    private RepoInfoResponse getRepoInfo(Repo repo) {
        return null;
    }
}
