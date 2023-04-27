package com.msp31.storage1c.domain.entity.repo;

import com.msp31.storage1c.domain.entity.account.User;
import com.msp31.storage1c.domain.entity.repo.model.RepoModel;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.util.*;

@Entity
@Table(name = "repositories")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Repo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    String directoryName;

    @ManyToOne
    @JoinColumn(name = "owner_user")
    User owner;

    @ManyToOne
    @JoinColumn(name = "default_access_level")
    RepoAccessLevel defaultAccessLevel;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    Calendar createdOn;

    @OneToMany(mappedBy = "repo", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<RepoUserAccess> users = new HashSet<>();

    public void addUser(RepoUserAccess userAccess) {
        users.add(userAccess);
        userAccess.setRepo(this);
    }

    public void removeUser(RepoUserAccess userAccess) {
        users.remove(userAccess);
        userAccess.setRepo(null);
    }

    public static Repo createFromModel(RepoModel model) {
        return Repo.builder()
                .users(new HashSet<>())
                .name(model.getName())
                .directoryName(model.getDirectoryName())
                .owner(model.getOwner())
                .defaultAccessLevel(model.getDefaultAccessLevel()).build();
    }
}
