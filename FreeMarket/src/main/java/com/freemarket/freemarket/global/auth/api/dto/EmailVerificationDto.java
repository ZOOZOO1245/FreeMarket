package com.freemarket.freemarket.global.auth.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class EmailVerificationDto {

    @Schema(description = "이메일 인증 요청")
    public record EmailVerificationRequest(
           @Schema(description = "학교 이메일 주소", example = "student@office.skhu.ac.kr")
           @NotBlank(message = "이메일은 필수 입력값입니다.")
           @Email(message = "이메일 형식이 올바르지 않습니다.")
           String email
    ) {}

    @Schema(description = "이메일 인증 코드 확인 요청")
    public record VerificationCodeRequest(
            @Schema(description = "학교 이메일 주소", example = "student@office.skhu.ac.kr")
            @NotBlank(message = "이메일은 필수 입력값입니다.")
            @Email(message = "이메일 형식이 올바르지 않습니다.")
            String email,

            @Schema(description = "6자리 인증 코드", example = "123456")
            @NotBlank(message = "인증 코드는 필수 입력값입니다.")
            String verificationCode
    ) {}

    @Schema(description = "이메일 인증 응답")
    public record EmailVerificationResponse(
            @Schema(description = "처리 성공 여부", example = "true")
            boolean success,
            
            @Schema(description = "응답 메시지", example = "인증 이메일이 발송되었습니다.")
            String message
    ) {}
}
