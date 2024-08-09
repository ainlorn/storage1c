package com.msp31.storage1c.domain.entity.repo;

import com.msp31.storage1c.domain.entity.repo.model.RepoCommitModel;
import com.msp31.storage1c.domain.entity.repo.model.RepoFileModel;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "repository_files")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RepoFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "repo_id")
    Repo repo;

    String path;

    @Builder.Default
    String description = "";

    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    Set<RepoFileTag> tags = new HashSet<>();

    public void addTag(RepoFileTag tag) {
        tags.add(tag);
        tag.setFile(this);
    }

    public void removeTag(RepoFileTag tag) {
        tags.remove(tag);
        tag.setFile(null);
    }

    public static RepoFile createFromModel(RepoFileModel model) {
        return RepoFile.builder()
                .repo(model.getRepo())
                .path(model.getPath())
                .description(model.getDescription()).build();
    }
}
