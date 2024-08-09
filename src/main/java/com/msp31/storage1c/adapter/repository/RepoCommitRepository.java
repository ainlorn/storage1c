package com.msp31.storage1c.adapter.repository;

import com.msp31.storage1c.domain.entity.repo.Repo;
import com.msp31.storage1c.domain.entity.repo.RepoCommit;
import com.msp31.storage1c.domain.entity.repo.RepoFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RepoCommitRepository extends JpaRepository<RepoCommit, Long> {
    Optional<RepoCommit> findByRepoAndCommitId(Repo repo, String commitId);

    List<RepoCommit> findByRepoAndCommitIdIn(Repo repo, List<String> commitIds);

    @Query(nativeQuery = true,
    value = "select * from repository_commits where repo_id=:repoId and id in " +
            "(select commit_id from repository_commit_tags " +
            "where UPPER(tag) in :tags group by commit_id having count(commit_id) = :cnt)")
    List<RepoCommit> findCommitsInRepoByAllTags(@Param("repoId") long repoId,
                                                @Param("tags") List<String> tagsUpper,
                                                @Param("cnt") int countTags);
}
