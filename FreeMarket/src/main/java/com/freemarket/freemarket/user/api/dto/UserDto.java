package com.freemarket.freemarket.user.api.dto;

import com.freemarket.freemarket.user.domain.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public class UserDto {

    public record ProfileUpdateRequest(
            @NotBlank(message = "이름은 필수 입력값입니다.")
            String name,

            @Pattern(regexp = "^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$", message = "휴대폰 번호 형식이 올바르지 않습니다.")
            String phone
    ) {
    }

    public record PasswordChangeRequest(
            @NotBlank(message = "현재 비밀번호는 필수 입력값입니다.")
            String currentPassword,

            @NotBlank(message = "새 비밀번호는 필수 입력값입니다.")
            @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
            String newPassword
    ) {
    }

    @Builder
    public record UserResponse(
            Long id,
            String email,
            String name,
            String phone,
            String role
    ) {
        public static UserResponse from(User user) {
            return UserResponse.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .phone(user.getPhone())
                    .role(user.getRole().getDisplayName())
                    .build();
        }
    }
}
