package com.example.project.user.service;

import com.example.project.global.auth.JwtProvider;
import com.example.project.global.exception.CustomException;
import com.example.project.global.exception.type.UserErrorCode;
import com.example.project.user.domain.User;
import com.example.project.user.dto.AdminRequestDto;
import com.example.project.user.dto.UserResponseDto;
import com.example.project.user.repository.UserRepository;
import com.example.project.user.type.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    @Value("${admin.secret.key}")
    private String adminSecretKey;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDto createAdmin(AdminRequestDto adminRequestDto) {
        if (!adminSecretKey.equals(adminRequestDto.getSecretKey())) { // 보안 키 검증
            throw new CustomException(UserErrorCode.ACCESS_DENIED);
        }

        if (userRepository.existsByUsername(adminRequestDto.getUsername())) {
            throw new CustomException(UserErrorCode.USER_ALREADY_EXISTS);
        }

        Set<UserRole> roles = new HashSet<>();
        roles.add(UserRole.ADMIN);

        User user = User.builder()
                .username(adminRequestDto.getUsername())
                .password(passwordEncoder.encode(adminRequestDto.getPassword()))
                .nickname(adminRequestDto.getNickname())
                .roles(roles)
                .build();

        User savedUser = userRepository.save(user);
        return UserResponseDto.toDto(savedUser);
    }


    @Transactional
    public UserResponseDto updateRole(Long id, User user) {
        if (user.getRoles().stream().noneMatch(role -> role == UserRole.ADMIN)) { // ✅ 수정: contains 대신 stream().noneMatch() 사용
            throw new CustomException(UserErrorCode.ACCESS_DENIED);
        }

        User findUser = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        findUser.getRoles().clear(); // ✅ 기존 역할 삭제
        findUser.getRoles().add(UserRole.ADMIN); // ✅ ADMIN 추가

        log.info("✅ 변경된 유저 정보: username={}, roles={}", findUser.getUsername(), findUser.getRoles());

        User savedUser = userRepository.save(findUser);
        UserResponseDto response = UserResponseDto.toDto(savedUser);

        log.info("🚀 최종 반환할 DTO: {}", response);

        return response;
    }

}
