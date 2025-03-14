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
public class User {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String nickname;
    @Getter
    private boolean deleted = false;

    @ElementCollection
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<UserRole> roles = new HashSet<>();

    @Builder
    public User(String username, String password, String nickname, Set<UserRole> roles, boolean deleted) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.roles = roles;
        this.deleted = deleted;
    }

    public void update(String newPassword, String nickname) {
        this.password = newPassword;
        this.nickname = nickname;
    }

    public void softDelete() {
        this.deleted = true;
    }

    public void setRoles(HashSet<UserRole> userRoles) {
        this.roles = userRoles;
    }
}
