package com.freemarket.freemarket.product.domain;

public enum ProductCategory {
    BOOKS("교재/서적"),
    ELECTRONICS("전자기기"),
    FASHION("의류/패션"),
    BEAUTY("화장품/미용"),
    SPORTS("스포츠/레저"),
    HOUSEHOLD("생활용품"),
    HOBBY("취미/게임"),
    OTHERS("기타");

    private final String displayName;

    ProductCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
