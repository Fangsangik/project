package com.example.project.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminRequestDto {
    @NotBlank(message = "이름 필수값 입니다.")
    private String username;
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
    private String nickname;

    @NotBlank(message = "시크릿키를 입력해주세요.")
    private String secretKey;

    public AdminRequestDto(String username, String password, String nickname, String secretKey) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.secretKey = secretKey;
    }
}
