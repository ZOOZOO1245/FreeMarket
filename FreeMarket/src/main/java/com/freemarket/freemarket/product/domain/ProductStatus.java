package com.freemarket.freemarket.product.domain;

public enum ProductStatus {
    ACTIVE("판매중"),
    SOLD_OUT("품절"),
    DISCONTINUED("판매 중단"),
    PENDING("승인 대기");

    private final String displayName;

    ProductStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
