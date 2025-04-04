package com.freemarket.freemarket.global.exception;

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

    public UserException(String message, HttpStatus status, String errorCode) {
        super(message, status, errorCode);
    }
}
