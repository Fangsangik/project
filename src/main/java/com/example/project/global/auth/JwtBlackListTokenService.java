package com.example.project.global.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class JwtBlackListTokenService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String BLACKLIST_PREFIX = "blacklisted";

    /**
     * 토큰을 블랙리스트에 추가
     *
     * @param token      블랙리스트에 추가할 토큰
     * @param expiration 토큰 만료 시간
     */
    public void addBlackList(String token, long expiration) {
        String key = token.startsWith(BLACKLIST_PREFIX) ? token : BLACKLIST_PREFIX + "." + token; // 중복 방지
        redisTemplate.opsForValue().set(key, "blacklisted", expiration, TimeUnit.MILLISECONDS);
    }

    /**
     * 토큰이 블랙리스트에 있는지 확인
     * @param token
     * @return
     */
    public boolean isBlackListed(String token) {
        return redisTemplate.hasKey(BLACKLIST_PREFIX + token);
    }
}
