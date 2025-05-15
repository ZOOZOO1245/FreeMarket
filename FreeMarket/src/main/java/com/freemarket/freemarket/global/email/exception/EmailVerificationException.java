package com.freemarket.freemarket.global.email.exception;

import com.freemarket.freemarket.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class EmailVerificationException extends BaseException {

    public static class EmailNotVerifiedException extends EmailVerificationException {
        public EmailNotVerifiedException() {
            super("학교 이메일 인증이 필요합니다.", HttpStatus.FORBIDDEN, "EMAIL_NOT_VERIFIED");
        }
    }
    public static class VerificationNotFoundException extends EmailVerificationException {
        public VerificationNotFoundException(String email) {
            super("인증 정보를 찾을 수 없습니다: " + email, HttpStatus.NOT_FOUND, "VERIFICATION_NOT_FOUND");
        }
    }

    public static class InvalidEmailDomainException extends EmailVerificationException {
        public InvalidEmailDomainException(String email) {
            super("학교 이메일만 사용 가능합니다: " + email, HttpStatus.BAD_REQUEST, "INVALID_EMAIL_DOMAIN");
        }
    }

    public static class VerificationCodeExpiredException extends EmailVerificationException {
        public VerificationCodeExpiredException() {
            super("인증 코드가 만료되었습니다.", HttpStatus.BAD_REQUEST, "VERIFICATION_CODE_EXPIRED");
        }
    }

    public static class InvalidVerificationCodeException extends EmailVerificationException {
        public InvalidVerificationCodeException() {
            super("인증 코드가 일치하지 않습니다.", HttpStatus.BAD_REQUEST, "INVALID_VERIFICATION_CODE");
        }
    }

    public EmailVerificationException(String message, HttpStatus status, String errorCode) {
        super(message, status, errorCode);
    }
}
