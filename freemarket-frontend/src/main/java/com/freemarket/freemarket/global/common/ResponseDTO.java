package com.freemarket.freemarket.global.common;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ResponseDTO<T> {
    private final boolean success;
    private final int status;
    private final String message;
    private final T data;
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
