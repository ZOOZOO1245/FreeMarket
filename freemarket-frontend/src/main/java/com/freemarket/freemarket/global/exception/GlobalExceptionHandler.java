package com.freemarket.freemarket.global.exception;

import com.freemarket.freemarket.global.common.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // BaseException 처리
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ResponseDTO<ErrorResponse>> handleBaseException(BaseException e) {
        log.error("BaseException: {}", e.getMessage(), e);

        ErrorResponse errorResponse = new ErrorResponse(
                e.getStatus().value(),
                e.getMessage(),
                e.getErrorCode(),
                LocalDateTime.now(),
                List.of()
        );

        return ResponseEntity.status(e.getStatus())
                .body(ResponseDTO.error(e.getStatus().value(), e.getMessage(), errorResponse));
    }

    // Spring Security 인증 예외 처리
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ResponseDTO<ErrorResponse>> handleAuthenticationException(AuthenticationException e) {
        log.error("AuthenticationException: {}", e.getMessage(), e);

        String message = "인증에 실패했습니다.";
        if (e instanceof BadCredentialsException) {
            message = "이메일 또는 비밀번호가 올바르지 않습니다.";
        }

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                message,
                "AUTH_FAILED",
                LocalDateTime.now(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseDTO.error(HttpStatus.UNAUTHORIZED.value(), message, errorResponse));
    }

    // 접근 거부 예외 처리
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseDTO<ErrorResponse>> handleAccessDeniedException(AccessDeniedException e) {
        log.error("AccessDeniedException: {}", e.getMessage(), e);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "접근 권한이 없습니다.",
                "ACCESS_DENIED",
                LocalDateTime.now(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ResponseDTO.error(HttpStatus.FORBIDDEN.value(), "접근 권한이 없습니다.", errorResponse));
    }

    // 유효성 검증 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDTO<ErrorResponse>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException: {}", e.getMessage(), e);

        List<ErrorResponse.FieldError> fieldErrors = e.getFieldErrors().stream()
                .map(fieldError -> new ErrorResponse.FieldError(
                        fieldError.getField(),
                        fieldError.getRejectedValue() != null ? fieldError.getRejectedValue().toString() : "",
                        fieldError.getDefaultMessage()
                ))
                .toList();

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "입력값이 올바르지 않습니다.",
                "INVALID_INPUT",
                LocalDateTime.now(),
                fieldErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseDTO.error(HttpStatus.BAD_REQUEST.value(), "입력값이 올바르지 않습니다.", errorResponse));
    }

    // 바인딩 예외 처리
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ResponseDTO<ErrorResponse>> handleBindException(BindException e) {
        log.error("BindException: {}", e.getMessage(), e);

        List<ErrorResponse.FieldError> fieldErrors = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ErrorResponse.FieldError(
                        fieldError.getField(),
                        fieldError.getRejectedValue() != null ? fieldError.getRejectedValue().toString() : "",
                        fieldError.getDefaultMessage()
                ))
                .toList();

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "입력값이 올바르지 않습니다.",
                "INVALID_INPUT",
                LocalDateTime.now(),
                fieldErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseDTO.error(HttpStatus.BAD_REQUEST.value(), "입력값이 올바르지 않습니다.", errorResponse));
    }

    // 그 외 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO<ErrorResponse>> handleException(Exception e) {
        log.error("Unexpected Exception: {}", e.getMessage(), e);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "서버 내부 오류가 발생했습니다.",
                "INTERNAL_SERVER_ERROR",
                LocalDateTime.now(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDTO.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 내부 오류가 발생했습니다.", errorResponse));
    }

}
