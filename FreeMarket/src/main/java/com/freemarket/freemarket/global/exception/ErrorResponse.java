package com.freemarket.freemarket.global.exception;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
public record ErrorResponse(
        int status,
        String message,
        String errorCode,
        LocalDateTime timestamp,
        List<FieldError> errors
) {

    @Builder
    public ErrorResponse {
        // Record의 컴팩트 생성자에서 기본값 설정
        if (errors == null) {
            errors = new ArrayList<>();
        }
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    @Builder
    public record FieldError(
            String field,
            String value,
            String reason
    ) {}
}
