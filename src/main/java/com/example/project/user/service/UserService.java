package com.example.project.user.service;

import com.example.project.global.auth.*;
import com.example.project.global.exception.CustomException;
import com.example.project.global.exception.type.AuthErrorCode;
import com.example.project.global.exception.type.UserErrorCode;
import com.example.project.user.domain.User;
import com.example.project.user.dto.*;
import com.example.project.user.repository.UserRepository;
import com.example.project.user.type.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final JwtRefreshTokenService jwtRefreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final JwtBlackListTokenService jwtBlackListTokenService;

    /**
     * 회원가입
     *
     * @param userRequestDto 회원가입 요청 DTO
     * @return UserResponseDto 회원가입 응답 DTO
     */
    @Transactional
    public UserResponseDto createUser(UserRequestDto userRequestDto) {

        if (userRepository.existsByUsernameAndDeletedFalse(userRequestDto.getUsername())) {
            throw new CustomException(UserErrorCode.USER_ALREADY_EXISTS);
        }

        if (userRepository.findByUsername(userRequestDto.getUsername()).isPresent()) {
            throw new CustomException(UserErrorCode.USER_DELETED);
        }


        User user = User.builder()
                .username(userRequestDto.getUsername())
                .password(passwordEncoder.encode(userRequestDto.getPassword()))
                .nickname(userRequestDto.getNickname())
                .roles(new HashSet<>(Collections.singletonList(UserRole.USER)))
                .deleted(false)
                .build();

        User savedUser = userRepository.save(user);

        return UserResponseDto.toDto(savedUser);
    }

    /**
     * 로그인
     *
     * @param requestDto 로그인 요청 DTO
     * @return LoginResponseDto 로그인 응답 DTO
     */
    @Transactional
    public LoginResponseDto login(LoginRequestDto requestDto) {

        User user = userRepository.findByUsername(requestDto.getUsername())
                .orElseThrow(() -> {
                    return new CustomException(UserErrorCode.USER_NOT_FOUND);
                });

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new CustomException(UserErrorCode.INVALID_CREDENTIALS);
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), requestDto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateAccessToken(authentication);
        String refreshToken = jwtProvider.generateRefreshToken(authentication);

        jwtRefreshTokenService.saveRefreshToken(user.getUsername(), refreshToken);
        return LoginResponseDto.toDto(token, refreshToken);
    }

    /**
     * 로그아웃
     */
    @Transactional
    public void logout(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.debug("Authentication 객체: {}", authentication);

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            log.error("로그아웃 실패: 인증 객체가 존재하지 않거나 CustomUserDetails가 아닙니다.");
            throw new CustomException(AuthErrorCode.UNAUTHORIZED_TOKEN);
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        log.info("로그아웃: {}", userDetails.getUsername());

        // 요청에서 기존 액세스 토큰을 가져오기
        String accessToken = getTokenFromRequest(request);
        if (accessToken == null || !jwtProvider.validToken(accessToken)) {
            log.error("로그아웃 실패: 유효한 액세스 토큰이 제공되지 않음");
            throw new CustomException(AuthErrorCode.UNAUTHORIZED_TOKEN);
        }

        long expiration = jwtProvider.getExpiration(accessToken) - System.currentTimeMillis();

        // 리프레시 토큰 삭제
        jwtRefreshTokenService.deleteRefreshToken(authentication);

        // 블랙리스트에 기존 액세스 토큰 추가
        jwtBlackListTokenService.addBlackList(accessToken, expiration);

        // SecurityContext 초기화
        SecurityContextHolder.clearContext();
        log.info("🚀 로그아웃 완료: 토큰 블랙리스트 추가 완료");
    }

    /**
     * 회원 정보 조회
     *
     * @param user 회원 정보
     * @return UserResponseDto 회원 정보 응답 DTO
     */
    @Transactional(readOnly = true)
    public UserResponseDto findUser(User user) {
        User findUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        return UserResponseDto.toDto(findUser);
    }

    @Transactional
    public UserUpdateResponseDto updateUser(User user, UserUpdateRequestDto requestDto) {

        User findUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(requestDto.getOldPassword(), findUser.getPassword())) {
            throw new CustomException(UserErrorCode.PASSWORD_MISMATCH);
        }

        if (requestDto.getNewPassword().equals(requestDto.getOldPassword())) {
            throw new CustomException(UserErrorCode.PASSWORD_SAME);
        }

        user.update(passwordEncoder.encode(requestDto.getNewPassword()), requestDto.getNickname());

        User savedUser = userRepository.save(user);

        return UserUpdateResponseDto.toDto(savedUser);
    }

    @Transactional
    public void deleteUser(User user, DeleteRequestDto deleteRequestDto) {
        User findUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(deleteRequestDto.getPassword(), findUser.getPassword())) {
            throw new CustomException(UserErrorCode.PASSWORD_MISMATCH);
        }
        user.softDelete();
        userRepository.save(user);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String headerPrefix = AuthenticationScheme.generateType(AuthenticationScheme.BEARER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(headerPrefix)) {
            return bearerToken.substring(headerPrefix.length()).trim();
        }
        return null;
    }
}
