package com.msp31.storage1c.adapter.repository;

import com.msp31.storage1c.domain.entity.repo.Repo;
import com.msp31.storage1c.domain.entity.repo.RepoCommit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RepoCommitRepository extends JpaRepository<RepoCommit, Long> {
    Optional<RepoCommit> findByRepoAndCommitId(Repo repo, String commitId);
    List<RepoCommit> findByRepoAndCommitIdIn(Repo repo, List<String> commitIds);
}
