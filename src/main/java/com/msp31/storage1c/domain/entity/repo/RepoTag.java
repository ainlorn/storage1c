package com.msp31.storage1c.domain.entity.repo;

import com.msp31.storage1c.domain.entity.repo.model.RepoTagModel;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "repository_tags")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RepoTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "repo_id")
    Repo repo;

    String tag;

    public static RepoTag createFromModel(RepoTagModel model) {
        return RepoTag.builder()
                .repo(model.getRepo())
                .tag(model.getTag()).build();
    }
}
