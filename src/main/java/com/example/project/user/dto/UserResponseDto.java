package com.example.project.user.dto;

import com.example.project.user.domain.User;
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
    private List<String> roles;

    @Builder
    public UserResponseDto(String username, String nickname, List<String> roles) {
        this.username = username;
        this.nickname = nickname;
        this.roles = roles;
    }

    public static UserResponseDto toDto(User user) {
        List<String> rolesList = user.getRoles() != null
                ? user.getRoles().stream().map(Enum::name).collect(Collectors.toList())
                : Collections.emptyList();

        return UserResponseDto.builder()
                .username(user.getUsername())
                .nickname(user.getNickname())
                .roles(rolesList)
                .build();
    }

}
