package com.example.project.global.auth;

import com.example.project.user.domain.User;
import com.example.project.user.repository.UserRepository;
import com.example.project.user.type.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * JWT 제공자.
 * <p>토큰의 생성, 추출, 만료 확인 등의 기능.</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Getter
public class JwtProvider {

    @Value("${jwt.secret_key}")
    private String secret;

    @Getter
    @Value("${jwt.expiry-millis}")
    private long expiryMillis;

    @Getter
    @Value("${jwt.refresh-expiry-millis}")
    private long refreshExpiryMillis;

    private final UserRepository userRepository;


    /**
     * 인증 객체를 받아와 액세스 토큰을 생성하는 메서드.
     *
     * @param authentication 인증 객체
     * @return 생성된 액세스 토큰
     */
    public String generateAccessToken(Authentication authentication) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + this.expiryMillis);

        String username = authentication.getName();
        // UserRoles 조회 추가
        Set<UserRole> userRoles = getUserRoles(username);

        List<String> roles = userRoles.stream()
                .map(UserRole::getRoleName)
                .toList();

        String token = Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), Jwts.SIG.HS256)
                .compact();

        log.info("🔹 생성된 토큰: {}", token);
        return token;
    }


    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * 리프레시 토큰 생성.
     *
     * @param authentication 인증 객체
     * @return 생성된 리프레시 토큰
     */
    public String generateRefreshToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + this.refreshExpiryMillis);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), Jwts.SIG.HS256)
                .compact();
    }

    public long getExpiration(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getExpiration().getTime();
    }


    public boolean validToken(String token) {
        try {
            return !getClaims(token).getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
            return false;
        } catch (JwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    private Claims getClaims(String token) {
        if (!StringUtils.hasText(token)) {
            throw new MalformedJwtException("토큰이 비어 있습니다.");
        }

        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Set<UserRole> getUserRoles(String username) {
        return userRepository.findByUsername(username) // DB에서 User 조회
                .map(User::getRoles) // User 객체에서 역할(Role) 가져오기
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}

