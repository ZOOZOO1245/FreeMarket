package com.freemarket.freemarket.user.api;

import com.freemarket.freemarket.global.common.ResponseDTO;
import com.freemarket.freemarket.global.security.CustomUserDetails;
import com.freemarket.freemarket.user.api.dto.UserDto;
import com.freemarket.freemarket.user.application.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "사용자", description = "사용자 정보 관리 API")
public class UserController {

    private final UserService userService;

    @Operation(summary = "사용자 프로필 조회", description = "로그인한 사용자의 프로필 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "사용자 정보를 찾을 수 없음")
    })
    @GetMapping("/me")
    public ResponseEntity<ResponseDTO<UserDto.UserResponse>> getProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        // 현재 인증된 사용자의 프로필 조회

        log.info("사용자 프로필 조회: {}", userDetails.getUserId());
        UserDto.UserResponse response = userService.getUserProfile(userDetails.getUserId());
        return ResponseEntity.ok(ResponseDTO.success(response, "프로필 조회에 성공했습니다."));
    }

    @Operation(summary = "사용자 프로필 수정", description = "로그인한 사용자의 이름과 전화번호를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "사용자 정보를 찾을 수 없음")
    })
    @PatchMapping("/me")
    public ResponseEntity<ResponseDTO<UserDto.UserResponse>> updateProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "프로필 수정 정보", required = true)
            @Valid @RequestBody UserDto.ProfileUpdateRequest request) {

        log.info("사용자 프로필 수정: {}", userDetails.getUserId());
        UserDto.UserResponse response = userService.updateProfile(userDetails.getUserId(), request);
        return ResponseEntity.ok(ResponseDTO.success(response, "프로필이 성공적으로 수정되었습니다."));
    }

    @Operation(summary = "비밀번호 변경", description = "현재 비밀번호 확인 후 새 비밀번호로 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 현재 비밀번호 불일치"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "사용자 정보를 찾을 수 없음")
    })
    @PostMapping("/me/password")
    public ResponseEntity<ResponseDTO<Void>> changePassword(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "비밀번호 변경 정보", required = true)
            @Valid @RequestBody UserDto.PasswordChangeRequest request) {

        log.info("비밀번호 변경 요청: {}", userDetails.getUserId());
        userService.changePassword(userDetails.getUserId(), request);
        return ResponseEntity.ok(ResponseDTO.success(null, "비밀번호가 성공적으로 변경되었습니다."));
    }
}
