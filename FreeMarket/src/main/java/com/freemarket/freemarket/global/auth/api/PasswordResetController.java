package com.freemarket.freemarket.global.auth.api;

import com.freemarket.freemarket.global.auth.api.dto.PasswordDto;
import com.freemarket.freemarket.global.auth.application.PasswordResetService;
import com.freemarket.freemarket.global.common.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth/password")
@RequiredArgsConstructor
@Tag(name = "비밀번호 재설정", description = "비밀번호 찾기 및 재설정 관련 API")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @Operation(summary = "비밀번호 재설정 요청", description = "이메일 주소를 입력받아 비밀번호 재설정 링크를 발송합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 재설정 이메일 발송 성공"),
            @ApiResponse(responseCode = "404", description = "사용자 정보를 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/reset-request")
    public ResponseEntity<ResponseDTO<PasswordDto.PasswordResetResponse>> requestPasswordReset(
            @Parameter(description = "비밀번호 재설정 요청 정보", required = true)
            @Valid @RequestBody PasswordDto.PasswordResetRequest request) {
        log.info("비밀번호 재설정 요청: {}", request.email());
        passwordResetService.requestPasswordReset(request);

        PasswordDto.PasswordResetResponse response = PasswordDto.PasswordResetResponse.builder()
                .success(true)
                .message("비밀번호 재설정 이메일이 발송되었습니다.")
                .build();

        return ResponseEntity.ok(ResponseDTO.success(response, "비밀번호 재설정 이메일이 발송되었습니다."));
    }

    @Operation(summary = "비밀번호 재설정", description = "토큰과 새 비밀번호를 입력받아 비밀번호를 재설정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 재설정 성공"),
            @ApiResponse(responseCode = "404", description = "유효하지 않은 토큰"),
            @ApiResponse(responseCode = "400", description = "만료된 토큰 또는 잘못된 요청")
    })
    @PostMapping("/reset-verify")
    public ResponseEntity<ResponseDTO<PasswordDto.PasswordResetResponse>> resetPassword(
            @Parameter(description = "비밀번호 재설정 정보", required = true)
            @Valid @RequestBody PasswordDto.PasswordResetVerifyRequest request) {

        log.info("비밀번호 재설정 확인");
        passwordResetService.resetPassword(request);

        PasswordDto.PasswordResetResponse response = PasswordDto.PasswordResetResponse.builder()
                .success(true)
                .message("비밀번호가 성공적으로 재설정되었습니다.")
                .build();

        return ResponseEntity.ok(ResponseDTO.success(response, "비밀번호가 성공적으로 재설정되었습니다."));
    }

}
