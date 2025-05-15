package com.freemarket.freemarket.global.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "통합 응답 DTO")
public class ResponseDTO<T> {
    @Schema(description = "요청 성공 여부", example = "true")
    private final boolean success;
    
    @Schema(description = "HTTP 상태 코드", example = "200")
    private final int status;
    
    @Schema(description = "응답 메시지", example = "요청이 성공했습니다.")
    private final String message;
    
    @Schema(description = "응답 데이터")
    private final T data;
    
    @Schema(description = "응답 시간", example = "2025-04-21T12:00:00")
    private final LocalDateTime timestamp;

    public static <T> ResponseDTO<T> success(T data) {
        return ResponseDTO.<T>builder()
                .success(true)
                .status(200)
                .message("요청이 성공했습니다.")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static <T> ResponseDTO<T> success(T data, String message) {
        return ResponseDTO.<T>builder()
                .success(true)
                .status(200)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ResponseDTO<T> error(int status, String message) {
        return ResponseDTO.<T>builder()
                .success(false)
                .status(status)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ResponseDTO<T> error(int status, String message, T data) {
        return ResponseDTO.<T>builder()
                .success(false)
                .status(status)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
