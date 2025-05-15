package com.freemarket.freemarket.user.exception;

import com.freemarket.freemarket.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class UserException extends BaseException {

    public static class UserNotFoundException extends UserException {
        public UserNotFoundException(String email) {
            super("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + email, HttpStatus.NOT_FOUND, "USER_NOT_FOUND");
        }

        public UserNotFoundException(Long id) {
            super("해당 ID를 가진 사용자를 찾을 수 없습니다: " + id, HttpStatus.NOT_FOUND, "USER_NOT_FOUND");
        }
    }

    // 비활성화된 사용자 예외
    public static class UserDisabledException extends UserException {
        public UserDisabledException() {
            super("비활성화된 사용자입니다.", HttpStatus.FORBIDDEN, "USER_DISABLED");
        }
    }

    public static class PasswordMismatchException extends UserException {
        public PasswordMismatchException() {
            super("현재 비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST, "PASSWORD_MISMATCH");
        }
    }

    public static class PasswordSameAsOldException extends UserException {
        public PasswordSameAsOldException() {
            super("새 비밀번호는 현재 비밀번호와 달라야 합니다.", HttpStatus.BAD_REQUEST, "PASSWORD_SAME_AS_OLD");
        }
    }

    public UserException(String message, HttpStatus status, String errorCode) {
        super(message, status, errorCode);
    }
}
