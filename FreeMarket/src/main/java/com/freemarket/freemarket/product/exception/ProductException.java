package com.freemarket.freemarket.product.exception;

import com.freemarket.freemarket.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ProductException extends BaseException {

    public static class ProductNotFoundException extends ProductException {
        public ProductNotFoundException(Long productId) {
            super("해당 ID의 상품을 찾을 수 없습니다: " + productId, HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND");
        }
    }

    public static class SellerMismatchException extends ProductException {
        public SellerMismatchException() {
            super("해당 상품의 판매자가 아닙니다.", HttpStatus.FORBIDDEN, "SELLER_MISMATCH");
        }
    }

    public static class ProductCreationException extends ProductException {
        public ProductCreationException(String message) {
            super("상품 등록 중 오류가 발생했습니다: " + message, HttpStatus.INTERNAL_SERVER_ERROR, "PRODUCT_CREATION_FAILED");
        }
    }

    public static class ProductUpdateException extends ProductException {
        public ProductUpdateException(String message) {
            super("상품 수정 중 오류가 발생했습니다: " + message, HttpStatus.INTERNAL_SERVER_ERROR, "PRODUCT_UPDATE_FAILED");
        }
    }
    public static class AlreadySoldProductException extends ProductException {
        public AlreadySoldProductException() {
            super("이미 판매 완료된 상품입니다.", HttpStatus.BAD_REQUEST, "ALREADY_SOLD_PRODUCT");
        }
    }

    public static class NotSoldProductException extends ProductException {
        public NotSoldProductException() {
            super("판매 완료 상태가 아닌 상품은 취소할 수 없습니다.", HttpStatus.BAD_REQUEST, "NOT_SOLD_PRODUCT");
        }
    }

    public static class ProductNotAvailableException extends ProductException {
        public ProductNotAvailableException() {
            super("해당 상품은 구매할 수 없는 상태입니다.", HttpStatus.BAD_REQUEST, "PRODUCT_NOT_AVAILABLE");
        }
    }

    public static class ProductAccessDeniedException extends ProductException {
        public ProductAccessDeniedException() {
            super("해당 상품에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN, "PRODUCT_ACCESS_DENIED");
        }
    }

    public static class ProductStockException extends ProductException {
        public ProductStockException() {
            super("상품 재고가 부족합니다.", HttpStatus.BAD_REQUEST, "PRODUCT_STOCK_INSUFFICIENT");
        }
    }

    public static class CannotCancelSoldProductException extends ProductException {
        public CannotCancelSoldProductException(String message) {
            super(message, HttpStatus.BAD_REQUEST, "CANNOT_CANCEL_SOLD_PRODUCT");
        }
    }


    public ProductException(String message, HttpStatus status, String errorCode) {
        super(message, status, errorCode);
    }
}
