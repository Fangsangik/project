package com.example.project.user.service;

import com.example.project.global.auth.JwtProvider;
import com.example.project.global.auth.JwtRefreshTokenService;
import com.example.project.global.config.AdminPermissionChecker;
import com.example.project.global.exception.CustomException;
import com.example.project.global.exception.type.UserErrorCode;
import com.example.project.user.domain.User;
import com.example.project.user.dto.AdminRequestDto;
import com.example.project.user.dto.RoleChangeRequestDto;
import com.example.project.user.dto.UserResponseDto;
import com.example.project.user.repository.UserRepository;
import com.example.project.user.type.UserRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AdminPermissionChecker adminPermissionChecker;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private JwtRefreshTokenService refreshTokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AdminService adminService;

    private final String adminSecretKey = "ADMIN_SECRET_KEY";

    private User user;
    private User admin;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("test")
                .password("test")
                .nickname("test")
                .role(UserRole.USER)
                .deleted(false)
                .build();

        admin = User.builder()
                .username("admin")
                .password("admin")
                .nickname("admin")
                .role(UserRole.ADMIN)
                .deleted(false)
                .build();

        //ReflectionTestUtils를 사용하여 private 필드에 값을 주입
        ReflectionTestUtils.setField(adminService, "adminSecretKey", adminSecretKey);
    }

    @Test
    void createAdmin_success() {
        AdminRequestDto requestDto = new AdminRequestDto("admin", "admin", "admin", "ADMIN_SECRET_KEY");

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        lenient().when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("encodedPassword");
        lenient().when(userRepository.findById(any())).thenReturn(Optional.of(admin));
        when(userRepository.save(any(User.class))).thenReturn(admin);

        UserResponseDto response = adminService.createAdmin(requestDto);

        Assertions.assertEquals(admin.getUsername(), response.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateRole_success() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        // permissionChecker.checkAdmin(admin) 기본적으로 “아무일도” 하지 않음
        RoleChangeRequestDto dto = new RoleChangeRequestDto(UserRole.ADMIN);

        // when
        UserResponseDto response = adminService.updateRole(1L, admin, dto);

        // then
        assertThat(response.getUserRole()).isEqualTo(UserRole.ADMIN);
        verify(userRepository).save(any());
    }

    @Test
    void updateRole_accessDenied() {
        // given
        lenient().when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        // permissionChecker가 USER일 때 예외 던지도록 설정
        doThrow(new CustomException(UserErrorCode.ACCESS_DENIED))
                .when(adminPermissionChecker).checkAdmin(user);
        RoleChangeRequestDto dto = new RoleChangeRequestDto(UserRole.ADMIN);

        // when / then
        CustomException ex = assertThrows(
                CustomException.class,
                () -> adminService.updateRole(1L, user, dto)
        );
        assertThat(ex.getCode()).isEqualTo(UserErrorCode.ACCESS_DENIED.getCode());
        verify(userRepository, never()).save(any());
    }
}