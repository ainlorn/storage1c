package com.msp31.storage1c.adapter.repository;

import com.msp31.storage1c.domain.entity.repo.Repo;
import com.msp31.storage1c.domain.entity.repo.RepoFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RepoFileRepository extends JpaRepository<RepoFile, Long> {
    Optional<RepoFile> findByRepoAndPath(Repo repo, String path);

    List<RepoFile> findByRepoAndPathIn(Repo repo, List<String> paths);

    @Query(nativeQuery = true,
    value = "select * from repository_files where repo_id=:repoId and id in" +
            "(select file_id from repository_file_tags\n" +
            "where UPPER(tag) in :tags group by file_id having count(file_id) = :cnt)")
    List<RepoFile> findFilesInRepoByAllTags(@Param("repoId") long repoId,
                                            @Param("tags") List<String> tagsUpper,
                                            @Param("cnt") int countTags);
}
