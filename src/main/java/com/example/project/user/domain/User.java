package com.example.project.user.domain;

import com.example.project.user.type.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String nickname;
    private boolean deleted = false;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Builder
    public User(String username, String password, String nickname, UserRole role, boolean deleted) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
        this.deleted = deleted;
    }

    public void update(String newPassword, String nickname) {
        this.password = newPassword;
        this.nickname = nickname;
    }

    public void softDelete() {
        this.deleted = true;
    }

    public void changeRole(UserRole role) {
        this.role = role;
    }
}
