package com.example.project.user.dto;

import com.example.project.user.type.UserRole;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserRequestDto {

    @NotBlank(message = "이름 필수값 입니다.")
    private String username;
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
    private String nickname;

    public UserRequestDto(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
    }
}
