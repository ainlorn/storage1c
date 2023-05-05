package com.msp31.storage1c.service.impl;

import com.msp31.storage1c.adapter.repository.RepoAccessLevelRepository;
import com.msp31.storage1c.adapter.repository.RepoRepository;
import com.msp31.storage1c.adapter.repository.RepoUserAccessRepository;
import com.msp31.storage1c.adapter.repository.UserRepository;
import com.msp31.storage1c.common.exception.*;
import com.msp31.storage1c.domain.dto.request.CreateRepoRequest;
import com.msp31.storage1c.domain.dto.response.RepoAccessLevelInfo;
import com.msp31.storage1c.domain.dto.response.RepoInfo;
import com.msp31.storage1c.domain.dto.response.RepoInfoResponse;
import com.msp31.storage1c.domain.dto.response.RepoUserAccessInfo;
import com.msp31.storage1c.domain.entity.repo.Repo;
import com.msp31.storage1c.domain.entity.repo.RepoAccessLevel;
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

import java.util.List;
import java.util.Objects;

@Service("repoService")
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
    public long getRepoId(String owner, String repoName) {
        var repo = repoRepository.findByOwnerUsernameAndName(owner, repoName);
        if (repo.isEmpty())
            throw new RepositoryNotFoundException();

        return repo.get().getId();
    }

    @Override
    public RepoAccessLevelInfo getAccessLevel(long repoId) {
        return repoMapper.createRepoAccessLevelInfoFrom(getAccessLevelInternal(repoId));
    }

    private RepoAccessLevel getAccessLevelInternal(long repoId) {
        var repo = repoRepository.findById(repoId);
        var user = userRepository.getCurrentUser();

        if (repo.isEmpty())
            throw new RepositoryNotFoundException();

        if (user != null) {
            var userAccess = repoUserAccessRepository.findByRepoAndUser(repo.get(), user);
            if (userAccess.isPresent())
                return userAccess.get().getAccessLevel();
        }

        return repo.get().getDefaultAccessLevel();
    }

    @Override
    @PreAuthorize("@repoService.getAccessLevel(#repoId).canView")
    public RepoInfoResponse getRepoInfo(long repoId) {
        var repo = repoRepository.getReferenceById(repoId);

        return repoMapper.createRepoInfoResponseFrom(repo, getAccessLevelInternal(repoId));
    }

    @Override
    public List<RepoInfo> getReposForUser(long userId) {
        var user = userRepository.findById(userId);
        if (user.isEmpty())
            throw new UserNotFoundException();

        var repos = repoRepository.findAllByOwner(user.get());

        return repos.stream()
                .filter(repo -> repo.getDefaultAccessLevel().isCanView())
                .map(repoMapper::createRepoInfoFrom)
                .toList();
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public List<RepoInfo> getReposForCurrentUser() {
        var user = userRepository.getCurrentUser();
        var repos = repoRepository.findAllByUserHasAccess(user.getId());
        return repos.stream()
                .map(repoMapper::createRepoInfoFrom)
                .toList();
    }

    @Override
    @PreAuthorize("@repoService.getAccessLevel(#repoId).canView")
    public List<RepoUserAccessInfo> getUsersForRepo(long repoId) {
        var repo = repoRepository.findById(repoId);
        if (repo.isEmpty())
            throw new RepositoryNotFoundException();

        return repo.get().getUsers().stream()
                .map(repoMapper::createRepoUserAccessInfoFrom)
                .toList();
    }

    @Override
    @PreAuthorize("@repoService.getAccessLevel(#repoId).canManage")
    public void addUserToRepo(long repoId, long userId, String roleName) {
        var repo = repoRepository.getReferenceById(repoId);
        var currentUser = userRepository.getCurrentUser();
        var targetUser = userRepository.findById(userId);
        if (targetUser.isEmpty())
            throw new UserNotFoundException();
        var role = repoAccessLevelRepository.findByName(roleName);
        if (role == null)
            throw new AccessLevelNotFoundException();

        if (repoUserAccessRepository.findByRepoAndUser(repo, targetUser.get()).isPresent())
            throw new UserAlreadyAddedException();

        if (role.isCanManage() && !Objects.equals(currentUser.getId(), repo.getOwner().getId()))
            throw new AccessDeniedException(); // only repo owner can add managers

        var userAccess = new RepoUserAccessModel(repo, targetUser.get(), role);
        repo.addUser(RepoUserAccess.createFromModel(userAccess));
        repoRepository.save(repo);
    }

    @Override
    @PreAuthorize("@repoService.getAccessLevel(#repoId).canManage")
    public void removeUserFromRepo(long repoId, long userId) {
        var repo = repoRepository.getReferenceById(repoId);
        var currentUser = userRepository.getCurrentUser();
        var targetUser = userRepository.findById(userId);
        if (targetUser.isEmpty())
            throw new UserNotFoundException();
        var userAccess = repoUserAccessRepository.findByRepoAndUser(repo, targetUser.get());
        if (userAccess.isEmpty())
            throw new UserNotFoundException();

        if (userAccess.get().getAccessLevel().isCanManage()
                && !Objects.equals(currentUser.getId(), repo.getOwner().getId()))
            throw new AccessDeniedException(); // only repo owner can remove managers

        repo.removeUser(userAccess.get());
        repoRepository.save(repo);
    }
}
