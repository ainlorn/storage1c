package com.msp31.storage1c.adapter.repository;

import com.msp31.storage1c.domain.entity.repo.Repo;
import com.msp31.storage1c.domain.entity.repo.RepoFile;
import com.msp31.storage1c.domain.entity.repo.RepoFileLock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface RepoFileLockRepository extends JpaRepository<RepoFileLock, Long> {
    Optional<RepoFileLock> findByFile(RepoFile file);
    Set<RepoFileLock> findAllByFile_Repo(Repo repo);
}
