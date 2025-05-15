package com.freemarket.freemarket.review.exception;

import com.freemarket.freemarket.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ReviewException extends BaseException {

    public static class ReviewNotFoundException extends ReviewException {
        public ReviewNotFoundException(Long reviewId) {
            super("해당 리뷰를 찾을 수 없습니다: " + reviewId, HttpStatus.NOT_FOUND, "REVIEW_NOT_FOUND");
        }
    }

    public static class ProductNotSoldException extends ReviewException {
        public ProductNotSoldException() {
            super("판매완료된 상품만 리뷰를 작성할 수 있습니다.", HttpStatus.BAD_REQUEST, "PRODUCT_NOT_SOLD");
        }
    }

    public static class SelfReviewException extends ReviewException {
        public SelfReviewException() {
            super("자신의 상품에 리뷰를 작성할 수 없습니다.", HttpStatus.BAD_REQUEST, "SELF_REVIEW_NOT_ALLOWED");
        }
    }

    public static class NotBuyerException extends ReviewException {
        public NotBuyerException() {
            super("해당 상품의 구매자만 리뷰를 작성할 수 있습니다.", HttpStatus.FORBIDDEN, "NOT_BUYER");
        }
    }

    public static class ReviewAlreadyExistsException extends ReviewException {
        public ReviewAlreadyExistsException() {
            super("이미 이 상품에 대한 리뷰가 존재합니다.", HttpStatus.CONFLICT, "REVIEW_ALREADY_EXISTS");
        }
    }

    public static class ReviewAccessDeniedException extends ReviewException {
        public ReviewAccessDeniedException() {
            super("해당 리뷰에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN, "REVIEW_ACCESS_DENIED");
        }
    }

    public ReviewException(String message, HttpStatus status, String errorCode) {
        super(message, status, errorCode);
    }
}
