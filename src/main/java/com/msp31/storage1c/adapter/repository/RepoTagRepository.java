package com.msp31.storage1c.adapter.repository;

import com.msp31.storage1c.domain.entity.repo.Repo;
import com.msp31.storage1c.domain.entity.repo.RepoTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepoTagRepository extends JpaRepository<RepoTag, Long> {
    Optional<RepoTag> findByRepoAndTag(Repo repo, String tag);
}
