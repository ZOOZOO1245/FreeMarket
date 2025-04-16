package com.freemarket.freemarket.order.domain;

import com.freemarket.freemarket.global.auditing.BaseEntity;
import com.freemarket.freemarket.oderItem.domain.OrderItem;
import com.freemarket.freemarket.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(nullable = false)
    private long totalPrice;

    @Column(nullable = false)
    private String recipientName;

    @Column(nullable = false)
    private String recipientPhone;

    @Column(nullable = false)
    private String shippingAddress;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Builder
    public Order(User user, OrderStatus status, long totalPrice, String recipientName, String recipientPhone, String shippingAddress, List<OrderItem> items) {
        this.user = user;
        this.status = status != null ? status : OrderStatus.PENDING;
        this.totalPrice = totalPrice;
        this.recipientName = recipientName;
        this.recipientPhone = recipientPhone;
        this.shippingAddress = shippingAddress;
        this.orderNumber = generateOrderNumber();
    }

    private String generateOrderNumber() {
        return "ORD" + System.currentTimeMillis();
    }

    // 주문 아이템 추가 메서 (양방향 관계 관리)
    public void addItem(OrderItem item) {
        items.add(item);
        item.connectToOrder(this);
    }

    // 주문 상태 변경
    public void changeStatus(OrderStatus status) {
        this.status = status;
    }

    // 주문 취소 메서드
    public void cancel() {
        if (this.status == OrderStatus.SHIPPING || this.status == OrderStatus.DELIVERED) {
            throw new IllegalArgumentException("이미 배송중이거나, 배송 완료된 주문은 취소할 수 없습니다.");
        }

        this.status = OrderStatus.CANCELED;

        // 재고 원복
        for (OrderItem item : items) {
            item.cancel();
        }
    }
}
