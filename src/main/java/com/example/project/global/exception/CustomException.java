package com.example.project.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomException extends RuntimeException {
    private final String code;
    private final String message;

    public CustomException(ExceptionType exceptionType) {
        super();
        this.code = exceptionType.getCode();
        this.message = exceptionType.getMessage();
    }
}
