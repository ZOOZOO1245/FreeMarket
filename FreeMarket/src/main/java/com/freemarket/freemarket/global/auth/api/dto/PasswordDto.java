package com.freemarket.freemarket.global.auth.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public class PasswordDto {

    public record PasswordResetRequest(
            @NotBlank(message = "이메일은 필수 입력값입니다.")
            @Email(message = "이메일 형식이 올바르지 않습니다.")
            String email
    ) {}

    public record PasswordResetVerifyRequest(
            @NotBlank(message = "토큰은 필수 입력값입니다.")
            String token,

            @NotBlank(message = "새 비밀번호는 필수 입력값입니다.")
            @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
            String newPassword
    ) {}

    @Builder
    public record PasswordResetResponse(
            boolean success,
            String message
    ) {}
}
