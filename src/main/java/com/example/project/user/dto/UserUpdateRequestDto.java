package com.example.project.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdateRequestDto {
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String newPassword;
    private String oldPassword;
    private String nickname;

    public UserUpdateRequestDto(String newPassword, String oldPassword, String nickname) {
        this.newPassword = newPassword;
        this.oldPassword = oldPassword;
        this.nickname = nickname;
    }
}
