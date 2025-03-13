package com.example.project.user.controller;

import com.example.project.global.util.UserInfo;
import com.example.project.user.domain.User;
import com.example.project.user.dto.AdminRequestDto;
import com.example.project.user.dto.UserResponseDto;
import com.example.project.user.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "관리자 회원가입", description = "관리자가 회원가입을 진행합니다.")
    @PostMapping
    public ResponseEntity<UserResponseDto> createAdmin(@RequestBody AdminRequestDto requestDto) {
        return ResponseEntity.ok(adminService.createAdmin(requestDto));
    }

    @Operation(summary = "유저 권한 변경", description = "관리자가 유저의 권한을 변경합니다.")
    @PatchMapping("/users/{id}/role")
    public ResponseEntity<UserResponseDto> updateRole(@AuthenticationPrincipal UserDetails userDetails,
                                                      @PathVariable Long id) {
        User user = UserInfo.getUser(userDetails);
        UserResponseDto response = adminService.updateRole(id, user);

        log.info("🚀 컨트롤러에서 반환할 DTO: {}", response);

        return ResponseEntity.ok(response);  // ✅ 서비스 호출 중복 제거
    }
}
