package com.msp31.storage1c.adapter.repository;

import com.msp31.storage1c.domain.entity.repo.Repo;
import com.msp31.storage1c.domain.entity.repo.RepoFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RepoFileRepository extends JpaRepository<RepoFile, Long> {
    Optional<RepoFile> findByRepoAndPath(Repo repo, String path);
    List<RepoFile> findByRepoAndPathIn(Repo repo, List<String> paths);
}
