package com.example.project.global.exception.dto;

import com.example.project.global.exception.CustomException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public class ExceptionResponseDto {
    private final Map<String, Object> error;

    public static ExceptionResponseDto from(CustomException ex) {
        return new ExceptionResponseDto(Map.of(
                "code", ex.getCode(),
                "message", ex.getMessage()
        ));
    }
}
