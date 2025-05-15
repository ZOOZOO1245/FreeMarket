package com.freemarket.freemarket.global.auth.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public class AuthDto {

    @Schema(description = "회원가입 요청")
    public record SignupRequest(
            @Schema(description = "이메일 주소", example = "user@example.com")
            @NotBlank(message = "이메일은 필수 입력값입니다.")
            @Email(message = "이메일 형식이 올바르지 않습니다.")
            String email,

            @Schema(description = "비밀번호 (최소 8자 이상)", example = "password123")
            @NotBlank(message = "비밀번호는 필수 입력값입니다.")
            @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
            String password,

            @Schema(description = "사용자 이름", example = "홍길동")
            @NotBlank(message = "이름은 필수 입력값입니다.")
            String name,

            @Schema(description = "휴대폰 번호", example = "010-1234-5678")
            @Pattern(regexp = "^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$", message = "휴대폰 번호 형식이 올바르지 않습니다.")
            String phone
    ) {
    }

    @Schema(description = "로그인 요청")
    public record LoginRequest(
            @Schema(description = "이메일 주소", example = "user@example.com")
            @NotBlank(message = "이메일은 필수 입력값입니다.")
            @Email(message = "이메일 형식이 올바르지 않습니다.")
            String email,

            @Schema(description = "비밀번호", example = "password123")
            @NotBlank(message = "비밀번호는 필수 입력값입니다.")
            String password
    ) {
    }

    @Builder
    @Schema(description = "토큰 응답")
    public record TokenResponse(
            @Schema(description = "JWT 액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            String accessToken,
            
            @Schema(description = "JWT 리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            String refreshToken,
            
            @Schema(description = "토큰 타입", example = "Bearer")
            String tokenType,
            
            @Schema(description = "액세스 토큰 만료 시간(초)", example = "3600")
            Long expiresIn
    ) {
    }

    @Schema(description = "토큰 갱신 요청")
    public record TokenRefreshRequest(
            @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @NotBlank(message = "리프레시 토큰은 필수 입력값입니다.")
            String refreshToken
    ) {
    }
}
