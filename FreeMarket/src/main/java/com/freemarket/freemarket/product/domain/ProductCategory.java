package com.freemarket.freemarket.product.domain;

public enum ProductCategory {
    TEST("테스트");

    private final String displayName;

    ProductCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
