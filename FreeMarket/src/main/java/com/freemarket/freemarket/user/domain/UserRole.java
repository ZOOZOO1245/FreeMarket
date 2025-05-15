package com.freemarket.freemarket.user.domain;

public enum UserRole {
    ROLE_USER("일반 사용자"),
    ROLE_SELLER("판매자"),
    ROLE_ADMIN("관리자");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
