package com.example.project.user.controller;

import com.example.project.global.util.UserInfo;
import com.example.project.user.domain.User;
import com.example.project.user.dto.AdminRequestDto;
import com.example.project.user.dto.UserResponseDto;
import com.example.project.user.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    /**
     * 관리자 회원가입
     */
    @Operation(summary = "관리자 회원가입", description = "관리자가 회원가입을 진행합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "관리자 회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 입력 값"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 관리자 계정")
    })
    @PostMapping
    public ResponseEntity<UserResponseDto> createAdmin(@RequestBody AdminRequestDto requestDto) {
        UserResponseDto responseDto = adminService.createAdmin(requestDto);

        log.info("✅ 관리자 회원가입 완료: username={}", responseDto.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * 유저 권한 변경
     */
    @Operation(summary = "유저 권한 변경", description = "관리자가 유저의 권한을 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "유저 권한 변경 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 요청 값"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "해당 유저를 찾을 수 없음")
    })
    @PatchMapping("/users/{id}/role")
    public ResponseEntity<UserResponseDto> updateRole(@AuthenticationPrincipal UserDetails userDetails,
                                                      @PathVariable Long id) {
        User admin = UserInfo.getUser(userDetails);
        UserResponseDto response = adminService.updateRole(id, admin);

        log.info("✅ 유저 권한 변경 완료: userId={}, 변경자={}", id, admin.getUsername());

        return ResponseEntity.ok(response);
    }
}
