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

    String description;

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
    @Builder.Default
    Set<RepoUserAccess> users = new HashSet<>();

    @OneToMany(mappedBy = "repo", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    Set<RepoTag> tags = new HashSet<>();

    @OneToMany(mappedBy = "repo", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    Set<RepoCommit> commits = new HashSet<>();

    @OneToMany(mappedBy = "repo", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    Set<RepoFile> files = new HashSet<>();

    public void addUser(RepoUserAccess userAccess) {
        users.add(userAccess);
        userAccess.setRepo(this);
    }

    public void removeUser(RepoUserAccess userAccess) {
        users.remove(userAccess);
        userAccess.setRepo(null);
    }

    public void addTag(RepoTag tag) {
        tags.add(tag);
        tag.setRepo(this);
    }

    public void removeTag(RepoTag tag) {
        tags.remove(tag);
        tag.setRepo(null);
    }

    public void addFile(RepoFile file) {
        files.add(file);
        file.setRepo(this);
    }

    public void removeFile(RepoFile file) {
        files.remove(file);
        file.setRepo(null);
    }

    public void addCommit(RepoCommit commit) {
        commits.add(commit);
        commit.setRepo(this);
    }

    public void removeCommit(RepoCommit commit) {
        commits.remove(commit);
        commit.setRepo(null);
    }

    public static Repo createFromModel(RepoModel model) {
        return Repo.builder()
                .name(model.getName())
                .description(model.getDescription())
                .directoryName(model.getDirectoryName())
                .owner(model.getOwner())
                .defaultAccessLevel(model.getDefaultAccessLevel()).build();
    }
}
