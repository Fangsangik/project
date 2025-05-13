package com.example.project.user.dto;

import com.example.project.user.domain.User;
import com.example.project.user.type.UserRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDto {
    private String username;
    private String nickname;
    private UserRole userRole;

    @Builder
    public UserResponseDto(String username, String nickname, UserRole userRole) {
        this.username = username;
        this.nickname = nickname;
        this.userRole = userRole;
    }

    public static UserResponseDto toDto(User user) {

        return UserResponseDto.builder()
                .username(user.getUsername())
                .nickname(user.getNickname())
                .userRole(user.getRole())
                .build();
    }

}
