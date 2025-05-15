package com.freemarket.freemarket.product.domain;

import com.freemarket.freemarket.global.auditing.BaseEntity;
import com.freemarket.freemarket.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private long price;

    @Column(nullable = false)
    private int stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    private User buyer;

    @Column
    private LocalDateTime soldDate; // 판매완료 날짜

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProductImage> images = new ArrayList<>();

    @Builder
    public Product(String name, String description, long price, int stock, ProductCategory category, ProductStatus status, User seller) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.status = status != null ? status : ProductStatus.ACTIVE;
        this.seller = seller;
    }


    // 상품 정보 업데이트
    public void update(String name, String description, long price, int stock, ProductCategory category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;

        // 재고 상태에 따라 상품 상태 업데이트
        if (stock > 0 && this.status == ProductStatus.SOLD_OUT) {
            this.status = ProductStatus.ACTIVE;
        } else if (stock == 0 && this.status == ProductStatus.ACTIVE) {
            this.status = ProductStatus.SOLD_OUT;
        }
    }

    // 상품 상태 변경
    public void changeStatus(ProductStatus status) {
        this.status = status;
    }

    // 이미지 추가 / 제거 편의 메서드
    public void addImage(ProductImage image) {
        this.images.add(image);
        image.setProduct(this);
    }

    public void removeImage(ProductImage image) {
        this.images.remove(image);
        image.setProduct(null);
    }

    // 상품 판매완료 처리 메서드
    public void markAsSold(User buyer) {
        this.status = ProductStatus.SOLD_OUT;
        this.buyer = buyer;
        this.soldDate = LocalDateTime.now();
        this.stock = 0;
    }

    // 판매 취소 메서드 추가
    public void cancelSold() {
        this.status = ProductStatus.ACTIVE;
        this.buyer = null;
        this.soldDate = null;
        this.stock = 1; // 중고 상품은 보통 1개
    }
    // 대표 썸네일 URL 조회
    public String getRepresentativeThumbnailUrl() {
        return this.images.stream()
                .filter(ProductImage::isThumbnail)
                .findFirst()
                .map(ProductImage::getThumbnailUrl)
                .orElse(this.images.isEmpty() ? null : this.images.get(0).getThumbnailUrl());
    }
}
