package com.example.project.user.service;

import com.example.project.global.config.AdminPermissionChecker;
import com.example.project.global.exception.CustomException;
import com.example.project.global.exception.type.UserErrorCode;
import com.example.project.user.domain.User;
import com.example.project.user.dto.AdminRequestDto;
import com.example.project.user.dto.RoleChangeRequestDto;
import com.example.project.user.dto.UserResponseDto;
import com.example.project.user.repository.UserRepository;
import com.example.project.user.type.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    @Value("${admin.secret.key}")
    private String adminSecretKey;

    private final AdminPermissionChecker adminPermissionChecker;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 관리자 계정 생성
     *
     * @param adminRequestDto 관리자 계정 생성 요청 DTO
     * @return UserResponseDto 관리자 계정 생성 응답 DTO
     */
    @Transactional
    public UserResponseDto createAdmin(AdminRequestDto adminRequestDto) {
        if (!adminSecretKey.equals(adminRequestDto.getSecretKey())) { // 보안 키 검증
            throw new CustomException(UserErrorCode.ACCESS_DENIED);
        }

        if (userRepository.existsByUsername(adminRequestDto.getUsername())) {
            throw new CustomException(UserErrorCode.USER_ALREADY_EXISTS);
        }

        User user = User.builder()
                .username(adminRequestDto.getUsername())
                .password(passwordEncoder.encode(adminRequestDto.getPassword()))
                .nickname(adminRequestDto.getNickname())
                .role(UserRole.ADMIN)
                .build();

        User savedUser = userRepository.save(user);
        return UserResponseDto.toDto(savedUser);
    }

    /**
     * 관리자 권한 변경
     * @param id
     * @param user
     * @return response
     */
    @Transactional
    public UserResponseDto updateRole(Long id, User user, RoleChangeRequestDto requestDto) {
        // 1) ADMIN 권한 확인 (단위 테스트 및 AOP 프록시 양쪽에서 검증)
        adminPermissionChecker.checkAdmin(user);

        // 2) 대상 유저 조회
        User target = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        // 3) 역할 변경
        target.changeRole(requestDto.getUserRole());

        // 4) 저장 및 DTO 변환 후 반환
        return UserResponseDto.toDto(userRepository.save(target));
    }
}
