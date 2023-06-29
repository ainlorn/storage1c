package com.msp31.storage1c.domain.entity.repo;

import com.msp31.storage1c.domain.entity.account.User;
import com.msp31.storage1c.domain.entity.repo.model.RepoFileLockModel;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Calendar;

@Entity
@Table(name = "repository_file_locks")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RepoFileLock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "file_id")
    RepoFile file;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    Calendar lockedOn;

    public static RepoFileLock createFromModel(RepoFileLockModel model) {
        return RepoFileLock.builder()
                .file(model.getFile())
                .user(model.getUser()).build();
    }
}
