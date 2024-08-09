package com.msp31.storage1c.domain.entity.repo;

import com.msp31.storage1c.domain.entity.repo.model.RepoCommitTagModel;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "repository_commit_tags")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RepoCommitTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "commit_id")
    RepoCommit commit;

    String tag;

    public static RepoCommitTag createFromModel(RepoCommitTagModel model) {
        return RepoCommitTag.builder()
                .commit(model.getCommit())
                .tag(model.getTag()).build();
    }
}
