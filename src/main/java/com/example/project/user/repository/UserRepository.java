package com.example.project.user.repository;

import com.example.project.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);

    Optional<User> findByUsername(@Param("username") String username);

    boolean existsByUsernameAndDeletedFalse(String username);
}
