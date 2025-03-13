package com.example.project.global.exception.type;

import com.example.project.global.exception.ExceptionType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ExceptionType {
    USER_ALREADY_EXISTS("USER_ALREADY_EXISTS", "이미 존재하는 사용자입니다."),
    USER_NOT_FOUND("USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    INVALID_CREDENTIALS("INVALID_CREDENTIALS", "아이디 또는 비밀번호가 올바르지 않습니다."),
    ACCESS_DENIED("ACCESS_DENIED", "접근 권한이 없습니다.");

    private final String code;
    private final String message;


    @Override
    public String getCode() {
        return this.name();
    }
}
