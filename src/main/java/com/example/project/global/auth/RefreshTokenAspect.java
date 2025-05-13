package com.example.project.global.auth;

import com.example.project.user.dto.LoginResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RefreshTokenAspect {
    private final JwtRefreshTokenService jwtRefreshTokenService;

    @AfterReturning(
            pointcut = "@annotation(com.example.project.global.auth.SaveRefreshToken)",
            returning = "dto"
    )
    public void saveRefreshToken(JoinPoint jp, Object dto) {
        try {
            LoginResponseDto r = (LoginResponseDto) dto;
            jwtRefreshTokenService.saveRefreshToken(r.getUsername(), r.getRefreshToken());
        } catch (Exception ex) {
            log.error("💥 리프레시 토큰 저장 실패 (무시하고 로그인은 성공 처리): ", ex);
        }
    }

    @Before("@annotation(com.example.project.global.auth.DeleteRefreshToken)")
    public void deleteRefreshToken(JoinPoint jp) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        jwtRefreshTokenService.deleteRefreshToken(auth);
    }
}

