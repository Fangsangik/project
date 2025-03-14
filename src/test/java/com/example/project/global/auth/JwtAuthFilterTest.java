package com.example.project.global.auth;

import com.example.project.user.service.AdminService;
import com.example.project.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest //Spring MVC 컨트롤러 테스트를 위한 어노테이션
@AutoConfigureMockMvc(addFilters = true) //MockMvc를 사용하기 위한 어노테이션
@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    //MockMvc를 사용하여 HTTP 요청을 모의 실행
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtBlackListTokenService jwtBlackListTokenService;

    @MockBean
    private AdminService adminService;

    @MockBean
    private UserService userService;

    // 테스트용 토큰 정의
    private final String validToken = "valid.jwt.token";
    private final String expiredToken = "expired.jwt.token";

    /**
     * 만료된 JWT 토큰으로 요청 시 401 Unauthorized 응답을 반환하는지 테스트
     * @throws Exception
     */
    @Test
    void requestWithInvalidToken_ShouldReturn401() throws Exception {
        when(jwtProvider.validToken(expiredToken)).thenReturn(false);

        // 임의의 endpoint에 대한 요청
        mockMvc.perform(get("/secure-endpoint")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());
    }

    /**
     * JWT 토큰 없이 요청 시 401 Unauthorized 응답을 반환하는지 테스트
     * @throws Exception
     */
    @Test
    void requestWithoutToken_ShouldReturn401() throws Exception {
        // 임의의 endpoint에 대한 요청
        mockMvc.perform(get("/secure-endpoint"))
                .andExpect(status().isUnauthorized());
    }
}
