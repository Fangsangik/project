package com.example.project.global.exception.type;

import com.example.project.global.exception.ExceptionType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ExceptionType {

    USER_ALREADY_EXISTS("USER_ALREADY_EXISTS", "이미 존재하는 사용자입니다.", HttpStatus.CONFLICT),
    USER_NOT_FOUND("USER_NOT_FOUND", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_CREDENTIALS("INVALID_CREDENTIALS", "아이디 또는 비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED("ACCESS_DENIED", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    PASSWORD_MISMATCH("PASSWORD_MISMATCH", "비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    PASSWORD_SAME("PASSWORD_SAME", "기존 비밀번호가 동일합니다.", HttpStatus.BAD_REQUEST),
    USER_DELETED("USER_DELETED", "탈퇴한 사용자입니다.", HttpStatus.FORBIDDEN);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    @Override
    public String getCode() {
        return this.name();
    }
}
