package com.msp31.storage1c.domain.entity.account;

import com.msp31.storage1c.domain.entity.account.model.UserModel;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Calendar;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String username;

    String fullName;

    String email;

    String password;

    // TODO create separate entity class
    Long role;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    Calendar createdOn;

    Boolean enabled = true;

    public static User createFromModel(UserModel model) {
        return User.builder()
                .username(model.getUsername())
                .fullName(model.getFullName())
                .email(model.getEmail())
                .password(model.getPassword())
                .role(model.getRole())
                .enabled(model.isEnabled()).build();
    }
}
