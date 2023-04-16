package com.msp31.storage1c.adapter.repository;

import com.msp31.storage1c.domain.entity.account.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> getByUsername(String username);
    Optional<User> getByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
