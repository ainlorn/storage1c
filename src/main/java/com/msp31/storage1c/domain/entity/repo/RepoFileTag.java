package com.msp31.storage1c.domain.entity.repo;

import com.msp31.storage1c.domain.entity.repo.model.RepoFileTagModel;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "repository_file_tags")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RepoFileTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "file_id")
    RepoFile file;

    String tag;

    public static RepoFileTag createFromModel(RepoFileTagModel model) {
        return RepoFileTag.builder()
                .file(model.getFile())
                .tag(model.getTag()).build();
    }
}
