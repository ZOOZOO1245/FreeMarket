package com.freemarket.freemarket.global.auth.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public class PasswordDto {

    @Schema(description = "비밀번호 재설정 요청")
    public record PasswordResetRequest(
            @Schema(description = "사용자 이메일 주소", example = "user@example.com")
            @NotBlank(message = "이메일은 필수 입력값입니다.")
            @Email(message = "이메일 형식이 올바르지 않습니다.")
            String email
    ) {}

    @Schema(description = "비밀번호 재설정 검증 요청")
    public record PasswordResetVerifyRequest(
            @Schema(description = "비밀번호 재설정 토큰", example = "1234567890abcdef")
            @NotBlank(message = "토큰은 필수 입력값입니다.")
            String token,

            @Schema(description = "새 비밀번호 (최소 8자 이상)", example = "newPassword123")
            @NotBlank(message = "새 비밀번호는 필수 입력값입니다.")
            @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
            String newPassword
    ) {}

    @Builder
    @Schema(description = "비밀번호 재설정 응답")
    public record PasswordResetResponse(
            @Schema(description = "처리 성공 여부", example = "true")
            boolean success,
            
            @Schema(description = "응답 메시지", example = "비밀번호가 성공적으로 변경되었습니다.")
            String message
    ) {}
}
