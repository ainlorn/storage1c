package com.msp31.storage1c.adapter.repository;

import com.msp31.storage1c.domain.entity.repo.RepoCommit;
import com.msp31.storage1c.domain.entity.repo.RepoCommitTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepoCommitTagRepository extends JpaRepository<RepoCommitTag, Long> {
    Optional<RepoCommitTag> findByCommitAndTag(RepoCommit commit, String tag);
}
