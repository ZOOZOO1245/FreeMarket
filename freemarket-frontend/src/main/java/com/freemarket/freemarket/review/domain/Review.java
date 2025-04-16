package com.freemarket.freemarket.review.domain;

import com.freemarket.freemarket.oderItem.domain.OrderItem;
import com.freemarket.freemarket.product.domain.Product;
import com.freemarket.freemarket.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "review")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    @Column(nullable = false)
    private int rating;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Builder
    public Review(User user, Product product, OrderItem orderItem, int rating, String content) {
        this.user = user;
        this.product = product;
        this.orderItem = orderItem;
        this.rating = rating;
        this.content = content;
    }

    // 리뷰 내용 업데이트 메서드
    public void update(int rating, String content) {
        this.rating = rating;
        this.content = content;
    }
}
