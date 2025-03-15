package com.example.project.global.auth;

import com.example.project.global.exception.CustomException;
import com.example.project.global.exception.type.UserErrorCode;
import com.example.project.user.domain.User;
import com.example.project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtRefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;
    private final static String REFRESH_TOKEN_PREFIX = "refresh";
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    /**
     * 리프레시 토큰을 저장.
     *
     * @param username 사용자 이름
     * @param token    리프레시 토큰
     */
    public void saveRefreshToken(String username, String token) {
        String key = REFRESH_TOKEN_PREFIX + username;
        log.info("🟢 saveRefreshToken() 호출됨 - Key: {}, 호출 스택: {}", key, Thread.currentThread().getStackTrace());
        redisTemplate.opsForValue().set(key, token, jwtProvider.getRefreshExpiryMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * 리프레시 토큰을 삭제.
     *
     * @param authentication 인증 객체
     */
    public void deleteRefreshToken(Authentication authentication) {
        if (authentication == null) {
            // 인증 객체가 null일 경우 로그에 경고 메시지 출력 후 종료
            log.warn("인증 객체가 존재하지 않습니다.");
            return;
        }

        // 삭제 시도
        try {
            String username = authentication.getName();

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

            String key = REFRESH_TOKEN_PREFIX + user.getUsername();

            Boolean deleted = redisTemplate.delete(key);

            if (Boolean.TRUE.equals(deleted)) {
                log.info("리프레시 토큰 삭제 username : {}", user.getUsername());
            } else {
                log.warn("해당하는 리프레시 토큰 조회 실패 username : {}", user.getUsername());
            }
        } catch (Exception ex) { //삭제 실패시 에러 반환
            log.error("리프레시 토큰 삭제 실패", ex);
            throw new RuntimeException("리프레시 토큰 삭제 실패", ex);
        }
    }
}
