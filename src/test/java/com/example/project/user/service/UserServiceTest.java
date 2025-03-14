package com.example.project.user.service;

import com.example.project.global.auth.JwtProvider;
import com.example.project.global.auth.JwtRefreshTokenService;
import com.example.project.global.exception.CustomException;
import com.example.project.global.exception.type.UserErrorCode;
import com.example.project.user.domain.User;
import com.example.project.user.dto.LoginRequestDto;
import com.example.project.user.dto.LoginResponseDto;
import com.example.project.user.dto.UserRequestDto;
import com.example.project.user.dto.UserResponseDto;
import com.example.project.user.repository.UserRepository;
import com.example.project.user.type.UserRole;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private JwtRefreshTokenService refreshTokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("test")
                .password("test")
                .nickname("test")
                .roles(Set.of(UserRole.USER))
                .deleted(false)
                .build();
    }

    @Test
    void create_success() {
        UserRequestDto requestDto = new UserRequestDto("test", "test", "test");

        when(userRepository.existsByUsernameAndDeletedFalse(requestDto.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDto userResponseDto = userService.createUser(requestDto);

        assertEquals(user.getUsername(), userResponseDto.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_alreadyExists() {
        UserRequestDto requestDto = new UserRequestDto("test", "test", "test");

        when(userRepository.existsByUsernameAndDeletedFalse(requestDto.getUsername())).thenReturn(true);

        CustomException customException = assertThrows(CustomException.class, () -> userService.createUser(requestDto));
        Assertions.assertThat(customException.getCode()).isEqualTo(UserErrorCode.USER_ALREADY_EXISTS.getCode());
    }

    @Test
    void login_success() {
        LoginRequestDto requestDto = new LoginRequestDto("test", "test");
        when(userRepository.findByUsername(requestDto.getUsername())).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches(requestDto.getPassword(), user.getPassword())).thenReturn(true);
        lenient().when(jwtProvider.generateAccessToken(any())).thenReturn("token");
        lenient().when(jwtProvider.generateRefreshToken(any())).thenReturn("refreshToken");

        LoginResponseDto loginResponseDto = userService.login(requestDto);
        Assertions.assertThat(loginResponseDto.getAccessToken()).isEqualTo("token");
        Assertions.assertThat(loginResponseDto.getRefreshToken()).isEqualTo("refreshToken");
    }

    @Test
    void login_invalidPassword() {
        LoginRequestDto requestDto = new LoginRequestDto("test", "test");
        when(userRepository.findByUsername(requestDto.getUsername())).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches(requestDto.getPassword(), user.getPassword())).thenReturn(false);

        CustomException exception = assertThrows(CustomException.class, () -> userService.login(requestDto));
        Assertions.assertThat(exception.getCode()).isEqualTo(UserErrorCode.INVALID_CREDENTIALS.getCode());
    }
}