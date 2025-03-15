package com.example.project.global.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtBlackListTokenServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private JwtBlackListTokenService jwtBlackListTokenService;

    @Test
    void addBlackList_ShouldAddTokenToRedis() {
        // Given
        String token = "token";
        long expirationTime = 60000L;
        String expectedKey = "blacklisted." + token;  // 실제 서비스 코드에서 사용하는 키 형식 적용
        String expectedValue = "LOGGED_OUT";

        // RedisTemplate의 opsForValue() 모킹
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // When
        jwtBlackListTokenService.addBlackList(token, expirationTime);

        // Then
        verify(valueOperations, times(1)).set(expectedKey, expectedValue, expirationTime, TimeUnit.MILLISECONDS);
    }
}
