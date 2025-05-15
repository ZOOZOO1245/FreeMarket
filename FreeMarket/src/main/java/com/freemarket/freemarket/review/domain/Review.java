package com.freemarket.freemarket.review.domain;

import com.freemarket.freemarket.global.auditing.BaseEntity;
import com.freemarket.freemarket.product.domain.Product;
import com.freemarket.freemarket.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reviews")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    private User reviewer; // 리뷰 작성자 (구매자)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id")
    private User targetUser; // 리뷰 대상자 (판매자)

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Builder
    public Review(User reviewer, User targetUser, Product product, Integer rating, String content) {
        this.reviewer = reviewer;
        this.targetUser = targetUser;
        this.product = product;
        this.rating = rating;
        this.content = content;
    }

    // 리뷰 내용 수정
    public void update(Integer rating, String content) {
        this.rating = rating;
        this.content = content;
    }
}
