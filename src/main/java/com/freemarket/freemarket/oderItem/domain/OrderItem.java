package com.freemarket.freemarket.oderItem.domain;

import com.freemarket.freemarket.global.auditing.BaseTimeEntity;
import com.freemarket.freemarket.order.domain.Order;
import com.freemarket.freemarket.product.domain.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // 주문 시점의 상품 정보
    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private long price;

    @Builder
    public OrderItem(Order order, Product product, String productName, int quantity, long price) {
        this.order = order;
        this.product = product;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    // 주문 생성 시 상품의 재고 감소
    public static OrderItem createOrderItem(Product product, int quantity) {
        product.decreaseStock(quantity);

        return OrderItem.builder()
                .product(product)
                .productName(product.getName())
                .quantity(quantity)
                .price(product.getPrice() * quantity)
                .build();
    }

    // 주문 취소 시 상품의 재고 원복
    public void cancel() {
        if (this.product != null) {
            this.product.increaseStock(this.quantity);
        }
    }

    public void connectToOrder(Order order) {
        this.order = order;
    }
}
