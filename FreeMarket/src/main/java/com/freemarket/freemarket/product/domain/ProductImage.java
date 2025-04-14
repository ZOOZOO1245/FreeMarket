package com.freemarket.freemarket.product.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_images")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private String thumbnailUrl;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private int displayOrder; // 표시 순서

    @Column(nullable = false)
    private boolean isThumbnail; // 대표 이미지 여부

    @Builder
    public ProductImage(Product product, String imageUrl, String thumbnailUrl, String originalFileName, int displayOrder, boolean isThumbnail) {
        this.product = product;
        this.imageUrl = imageUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.originalFileName = originalFileName;
        this.displayOrder = displayOrder;
        this.isThumbnail = isThumbnail;
    }

    // 연관관계 편의 메서드
    public void setProduct(Product product) {
        if (this.product != null) {
            this.product.getImages().remove(this);
        }
        this.product = product;
        if (product != null && !product.getImages().contains(this)) {
            product.getImages().add(this);
        }
    }

    // 썸네일 상태 설정 메서드
    public void setThumbnail(boolean thumbnail) {
        isThumbnail = thumbnail;
    }
}
