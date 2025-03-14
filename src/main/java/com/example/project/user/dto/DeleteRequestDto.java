package com.example.project.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DeleteRequestDto {
    private Long userId;
    private String password;

    public DeleteRequestDto(Long userId, String password) {
        this.userId = userId;
        this.password = password;
    }
}
