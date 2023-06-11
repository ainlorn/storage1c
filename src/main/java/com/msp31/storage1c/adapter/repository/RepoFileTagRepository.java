package com.msp31.storage1c.adapter.repository;

import com.msp31.storage1c.domain.entity.repo.RepoFile;
import com.msp31.storage1c.domain.entity.repo.RepoFileTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepoFileTagRepository extends JpaRepository<RepoFileTag, Long> {
    Optional<RepoFileTag> findByFileAndTag(RepoFile file, String tag);
}
