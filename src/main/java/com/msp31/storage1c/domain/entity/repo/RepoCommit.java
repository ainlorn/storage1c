package com.msp31.storage1c.domain.entity.repo;

import com.msp31.storage1c.domain.entity.repo.model.RepoCommitModel;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "repository_commits")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RepoCommit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "repo_id")
    Repo repo;

    String commitId;

    @OneToMany(mappedBy = "commit", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    Set<RepoCommitTag> tags = new HashSet<>();

    public void addTag(RepoCommitTag tag) {
        tags.add(tag);
        tag.setCommit(this);
    }

    public void removeTag(RepoCommitTag tag) {
        tags.remove(tag);
        tag.setCommit(null);
    }

    public static RepoCommit createFromModel(RepoCommitModel model) {
        return RepoCommit.builder()
                .repo(model.getRepo())
                .commitId(model.getCommitId())
                .build();
    }
}
