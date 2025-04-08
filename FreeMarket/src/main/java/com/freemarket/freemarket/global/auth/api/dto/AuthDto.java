package com.freemarket.freemarket.global.auth.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public class AuthDto {

    public record SignupRequest(
            @NotBlank(message = "이메일은 필수 입력값입니다.")
            @Email(message = "이메일 형식이 올바르지 않습니다.")
            String email,

            @NotBlank(message = "비밀번호는 필수 입력값입니다.")
            @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
            String password,

            @NotBlank(message = "이름은 필수 입력값입니다.")
            String name,

            @Pattern(regexp = "^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$", message = "휴대폰 번호 형식이 올바르지 않습니다.")
            String phone
    ) {
    }

    public record LoginRequest(
            @NotBlank(message = "이메일은 필수 입력값입니다.")
            @Email(message = "이메일 형식이 올바르지 않습니다.")
            String email,

            @NotBlank(message = "비밀번호는 필수 입력값입니다.")
            String password
    ) {
    }

    @Builder
    public record TokenResponse(
            String accessToken,
            String refreshToken,
            String tokenType,
            Long expiresIn
    ) {
    }

    public record TokenRefreshRequest(
            @NotBlank(message = "리프레시 토큰은 필수 입력값입니다.")
            String refreshToken
    ) {
    }
}
