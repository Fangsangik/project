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

    //Spring Data Redis에서 제공하는 Redis의 String 자료구조를 쉽게 다룰 수 있도록 도와주는 인스턴스
    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private JwtBlackListTokenService jwtBlackListTokenService;

    @Test
    void addBlackList_ShouldAddTokenToRedis() {
        // Given
        String token = "blacklisted.token";
        long expirationTime = 60000L;

        // RedisTemplate의 opsForValue() 모킹
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // When
        jwtBlackListTokenService.addBlackList(token, expirationTime);

        // Then
        verify(valueOperations, times(1)).set(token, "blacklisted", expirationTime, TimeUnit.MILLISECONDS);
    }
}
