package com.example.project.global.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtBlackListTokenService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String BLACKLIST_PREFIX = "blacklisted.";

    /**
     * 토큰을 블랙리스트에 추가
     *
     * @param token      블랙리스트에 추가할 토큰
     * @param expiration 토큰 만료 시간 (밀리초 단위)
     */
    public void addBlackList(String token, long expiration) {
        if (token == null || expiration <= 0) {
            log.warn("🚨 블랙리스트 추가 실패: 유효하지 않은 토큰 or 만료시간");
            return;
        }

        String key = BLACKLIST_PREFIX + token;

        try {
            redisTemplate.opsForValue().set(key, "LOGGED_OUT", expiration, TimeUnit.MILLISECONDS);
            log.info("✅ 블랙리스트에 추가된 토큰: {}, Key: {}, 만료 시간: {} ms", token, key, expiration);
        } catch (Exception e) {
            log.error("🚨 블랙리스트 추가 중 오류 발생", e);
        }
    }

    /**
     * 토큰이 블랙리스트에 있는지 확인
     * @param token 검사할 토큰
     * @return 블랙리스트 여부
     */
    public boolean isBlackListed(String token) {
        if (token == null) {
            log.warn("🚨 블랙리스트 조회 실패: 토큰이 null입니다.");
            return false;
        }

        String key = BLACKLIST_PREFIX + token;
        Boolean exists = redisTemplate.hasKey(key);
        log.info("🔎 블랙리스트 조회 결과 (토큰: {} / Key: {}): {}", token, key, exists);

        return Boolean.TRUE.equals(exists);
    }
}
