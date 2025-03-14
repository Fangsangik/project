package com.example.project.user.dto;

import com.example.project.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdateResponseDto {
    private String username;

    public UserUpdateResponseDto(String username) {
        this.username = username;
    }

    public static UserUpdateResponseDto toDto(User user) {
        return new UserUpdateResponseDto(user.getUsername());
    }
}
