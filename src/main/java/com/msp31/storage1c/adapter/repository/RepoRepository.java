package com.msp31.storage1c.adapter.repository;

import com.msp31.storage1c.domain.entity.account.User;
import com.msp31.storage1c.domain.entity.repo.Repo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RepoRepository extends JpaRepository<Repo, Long> {
    List<Repo> findAllByOwner(User owner);

    @Query(value = "SELECT r.* FROM repositories r JOIN repository_users ru on r.id = ru.repo_id " +
            "WHERE ru.user_id=?1", nativeQuery = true)
    List<Repo> findAllByUserHasAccess(long userId);

    Optional<Repo> findByOwnerAndName(User owner, String name);

    Optional<Repo> findByOwnerUsernameAndName(String ownerUsername, String repoName);
}
