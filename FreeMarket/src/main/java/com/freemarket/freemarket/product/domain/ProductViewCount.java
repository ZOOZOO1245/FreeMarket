package com.freemarket.freemarket.product.domain;

import com.freemarket.freemarket.global.auditing.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_view_counts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductViewCount extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "view_count_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", unique = true)
    private Product product;

    @Column(nullable = false)
    private Long count = 0L;

    @Builder
    public ProductViewCount(Product product, Long count) {
        this.product = product;
        this.count = 0L;
    }

    public void incrementCount() {
        this.count += 1;
    }
}
