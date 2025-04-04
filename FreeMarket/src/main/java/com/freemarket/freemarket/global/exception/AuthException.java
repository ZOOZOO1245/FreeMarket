package com.freemarket.freemarket.global.exception;

import org.springframework.http.HttpStatus;

public class AuthException extends BaseException {

    // 이메일 중복 예외
    public static class EmailDuplicateException extends AuthException {
        public EmailDuplicateException() {
            super("이미 사용 중인 이메일입니다.", HttpStatus.CONFLICT, "AUTH_EMAIL_DUPLICATE");
        }
    }

    // 인증 실패 예외
    public static class AuthenticationFailedException extends AuthException {
        public AuthenticationFailedException() {
            super("인증에 실패했습니다.", HttpStatus.UNAUTHORIZED, "AUTH_FAILED");
        }
    }

    // 리프레시 토큰 만료 예외
    public static class RefreshTokenExpiredException extends AuthException {
        public RefreshTokenExpiredException() {
            super("리프레시 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED, "REFRESH_TOKEN_EXPIRED");
        }
    }

    // 리프레시 토큰 없음 예외
    public static class RefreshTokenNotFoundException extends AuthException {
        public RefreshTokenNotFoundException() {
            super("리프레시 토큰을 찾을 수 없습니다.", HttpStatus.NOT_FOUND, "REFRESH_TOKEN_NOT_FOUND");
        }
    }

    public AuthException(String message, HttpStatus status, String errorCode) {
        super(message, status, errorCode);
    }
}
