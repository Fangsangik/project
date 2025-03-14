package com.example.project.global.exception.type;

import com.example.project.global.exception.ExceptionType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ExceptionType {

    UNAUTHORIZED_TOKEN("UNAUTHORIZED_TOKEN", "토큰이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED),
    AUTHENTICATION_REQUIRED("AUTHENTICATION_REQUIRED", "인증된 정보가 없습니다.", HttpStatus.UNAUTHORIZED);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    @Override
    public String getCode() {
        return this.name();
    }
}
