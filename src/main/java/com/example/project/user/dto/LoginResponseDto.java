package com.example.project.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginResponseDto {
    private String token;

    @Builder
    public LoginResponseDto(String token) {
        this.token = token;
    }

    public static LoginResponseDto toDto(String token) {
        return LoginResponseDto.builder()
                .token(token)
                .build();
    }
}
