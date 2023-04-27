package com.msp31.storage1c.adapter.repository;

import com.msp31.storage1c.domain.entity.repo.RepoAccessLevel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepoAccessLevelRepository extends JpaRepository<RepoAccessLevel, Long> {
    RepoAccessLevel findByName(String name);
}
