package com.msp31.storage1c.domain.entity.repo;

import com.msp31.storage1c.domain.entity.account.User;
import com.msp31.storage1c.domain.entity.repo.model.RepoUserAccessModel;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "repository_users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RepoUserAccess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "repo_id")
    Repo repo;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name = "access_level")
    RepoAccessLevel accessLevel;

    public static RepoUserAccess createFromModel(RepoUserAccessModel model) {
        return RepoUserAccess.builder()
                .repo(model.getRepo())
                .user(model.getUser())
                .accessLevel(model.getAccessLevel()).build();
    }
}
