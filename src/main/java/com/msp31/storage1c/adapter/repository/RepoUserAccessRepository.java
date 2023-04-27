package com.msp31.storage1c.adapter.repository;

import com.msp31.storage1c.domain.entity.account.User;
import com.msp31.storage1c.domain.entity.repo.Repo;
import com.msp31.storage1c.domain.entity.repo.RepoUserAccess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RepoUserAccessRepository extends JpaRepository<RepoUserAccess, Long> {
    List<RepoUserAccess> findAllByRepo(Repo repo);
    List<RepoUserAccess> findAllByUser(User user);
    Optional<RepoUserAccess> findByRepoAndUser(Repo repo, User user);
}
