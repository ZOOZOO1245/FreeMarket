package com.freemarket.freemarket.global.oauth2.exception;

import com.freemarket.freemarket.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class OAuth2Exception extends BaseException {

    public static class UnsupportedProviderException extends OAuth2Exception {
        public UnsupportedProviderException(String provider) {
            super("지원하지 않는 소셜 로그인입니다: " + provider, HttpStatus.BAD_REQUEST, "UNSUPPORTED_OAUTH_PROVIDER");
        }
    }

    public static class OAuth2AttributeParsingException extends OAuth2Exception {
        public OAuth2AttributeParsingException(String provider) {
            super(provider + " 사용자 정보를 파싱하는데 실패했습니다,", HttpStatus.INTERNAL_SERVER_ERROR, "OAUTH_ATTRIBUTE_PARSING_FAILED");
        }
    }

    public static class OAuth2ProcessingException extends OAuth2Exception {
        public OAuth2ProcessingException(String message) {
            super(message, HttpStatus.INTERNAL_SERVER_ERROR, "OAUTH_PROCESSING_FAILED");
        }
    }

    public static class EmailAlreadyExistsException extends OAuth2Exception {
        public EmailAlreadyExistsException(String email) {
            super("이미 가입된 이메일입니다: " + email, HttpStatus.CONFLICT, "EMAIL_ALREADY_EXISTS");
        }
    }

    public static class MissingAttributeException extends OAuth2Exception {
        public MissingAttributeException(String provider, String attribute) {
            super(provider + " 응답에서 필수 속성(" + attribute + ")을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST, "MISSING_OAUTH_ATTRIBUTE");
        }
    }

    public static class InvalidResponseException extends OAuth2Exception {
        public InvalidResponseException(String provider) {
            super(provider + " 응답 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST, "INVALID_OAUTH_RESPONSE");
        }
    }

    public OAuth2Exception(String message, HttpStatus status, String errorCode) {
        super(message, status, errorCode);
    }
}
