package com.freemarket.freemarket.order.domain;

public enum OrderStatus {
    PENDING("결제 대기"),
    PAID("결제 완료"),
    PREPARING("상품 준비중"),
    SHIPPING("배송중"),
    DELIVERED("배송 완료"),
    CANCELED("주문 취소");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
