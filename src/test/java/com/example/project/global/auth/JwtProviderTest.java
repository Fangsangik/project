package com.example.project.global.auth;

import com.example.project.user.domain.User;
import com.example.project.user.repository.UserRepository;
import com.example.project.user.type.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtProviderTest {

    @InjectMocks
    private JwtProvider jwtProvider;

    @Mock
    private UserRepository userRepository;

    // JWT secret key
    private final String secretKey = "d4e8002d9a40324a4bc163505c22a1a8e0e77eb53d59efbd4f6e5a91ceb17736";

    private User user;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .username("test")
                .password("test")
                .nickname("test")
                .role(UserRole.USER)
                .deleted(false)
                .build();

        // ReflectionTestUtils를 사용하여 private 필드에 값을 주입
        ReflectionTestUtils.setField(jwtProvider, "secret", secretKey);
        ReflectionTestUtils.setField(jwtProvider, "expiryMillis", 60000L);
        ReflectionTestUtils.setField(jwtProvider, "refreshExpiryMillis", 604800000L);

        // "test"라는 username을 가진 user를 반환하도록 설정
        lenient().when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
    }

    @Test
    void generateAccessToken() {
        // Spring Security의 Authentication 객체를 Mocking
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test");

        // JWT Token 생성
        String token = jwtProvider.generateAccessToken(authentication);
        assertNotNull(token);
        assertTrue(jwtProvider.validToken(token));
    }

    /**
     * 잘못된 JWT 서명으로 유효성 검증 실패 테스트
     */
    @Test
    void validateToken() {
        // 임의로 변조된 JWT Token
        String malformedToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0IiwiaWF0IjoxNjg2NjYwMDAwLCJleHAiOjE2ODY2NjM2MDB9.invalidsignature";

        assertFalse(jwtProvider.validToken(malformedToken));
    }


    /**
     * 만료된 JWT Token 유효성 검증 실패 테스트
     */
    @Test
    void validate_Expiration_false() {
        // 만료된 JWT Token
        String expiredToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0IiwiaWF0IjoxNjk5OTk5OTk5LCJleHAiOjE2OTk5OTk5OTl9.-fakeSign";

        assertFalse(jwtProvider.validToken(expiredToken));
    }
}