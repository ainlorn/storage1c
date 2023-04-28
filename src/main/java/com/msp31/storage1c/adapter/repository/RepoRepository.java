package com.msp31.storage1c.adapter.repository;

import com.msp31.storage1c.domain.entity.account.User;
import com.msp31.storage1c.domain.entity.repo.Repo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepoRepository extends JpaRepository<Repo, Long> {
    Optional<Repo> findByOwnerAndName(User owner, String name);

    Optional<Repo> findByOwnerUsernameAndName(String ownerUsername, String repoName);
}
