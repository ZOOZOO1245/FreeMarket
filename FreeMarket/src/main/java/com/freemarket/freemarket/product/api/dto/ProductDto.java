package com.freemarket.freemarket.product.api.dto;

import com.freemarket.freemarket.product.domain.Product;
import com.freemarket.freemarket.product.domain.ProductCategory;
import com.freemarket.freemarket.product.domain.ProductImage;
import com.freemarket.freemarket.product.domain.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

public class ProductDto {

    @Schema(description = "상품 생성 요청")
    public record CreateRequest(
            @Schema(description = "상품명", example = "새 노트북")
            @NotBlank(message = "상품명은 필수 입력값입니다.")
            String name,

            @Schema(description = "상품 설명", example = "거의 사용하지 않은 노트북입니다.")
            String description,

            @Schema(description = "가격", example = "1000000")
            @NotNull(message = "가격은 필수 입력값입니다.")
            @Min(value = 0)
            Long price,

            @Schema(description = "재고 수량", example = "1")
            @NotNull(message = "재고는 필수 입력값입니다.")
            @Min(value = 1, message = "재고는 최소 1개 이상이어야 합니다.")
            Integer stock,

            @Schema(description = "상품 카테고리", example = "ELECTRONICS")
            @NotNull(message = "카테고리는 필수 입력값입니다.")
            ProductCategory category
    ) {
    }

    @Schema(description = "상품 수정 요청")
    public record UpdateRequest(
            @Schema(description = "상품명", example = "새 노트북")
            @NotBlank(message = "상품명은 필수 입력값입니다.")
            String name,

            @Schema(description = "상품 설명", example = "거의 사용하지 않은 노트북입니다.")
            String description,

            @Schema(description = "가격", example = "900000")
            @NotNull(message = "가격은 필수 입력값입니다.")
            @Min(value = 100, message = "가격은 최소 100원 이상이어야 합니다.")
            Long price,

            @Schema(description = "재고 수량", example = "1")
            @NotNull(message = "재고는 필수 입력값입니다.")
            @Min(value = 1, message = "재고는 최소 1개 이상이어야 합니다.")
            Integer stock,

            @Schema(description = "상품 카테고리", example = "ELECTRONICS")
            @NotNull(message = "카테고리는 필수 입력값입니다.")
            ProductCategory category,

            @Schema(description = "상품 상태", example = "ACTIVE")
            ProductStatus status
    ) {
    }

    // 기본 상품 정보 (등록, 수정 결과용)
    @Builder
    @Schema(description = "기본 상품 정보 응답")
    public record ProductBaseResponse(
            @Schema(description = "상품 ID", example = "1")
            Long id,

            @Schema(description = "상품명", example = "새 노트북")
            String name,

            @Schema(description = "상품 설명", example = "거의 사용하지 않은 노트북입니다.")
            String description,

            @Schema(description = "가격", example = "1000000")
            Long price,

            @Schema(description = "재고 수량", example = "1")
            Integer stock,

            @Schema(description = "카테고리 표시명", example = "전자기기")
            String category,

            @Schema(description = "상품 상태", example = "판매중")
            String status,

            @Schema(description = "썸네일 이미지 URL", example = "https://example.com/thumbnail.jpg")
            String thumbnailUrl,

            @Schema(description = "이미지 URL 목록")
            List<String> imageUrls,

            @Schema(description = "판매자 ID", example = "123")
            Long sellerId,

            @Schema(description = "판매자 이름", example = "홍길동")
            String sellerName
    ) {
        public static ProductBaseResponse from(Product product) {
            return ProductBaseResponse.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .description(product.getDescription())
                    .price(product.getPrice())
                    .stock(product.getStock())
                    .category(product.getCategory().getDisplayName())
                    .status(product.getStatus().getDisplayName())
                    .thumbnailUrl(product.getRepresentativeThumbnailUrl())
                    .imageUrls(product.getImages().stream()
                            .map(ProductImage::getImageUrl)
                            .collect(Collectors.toList()))
                    .sellerId(product.getSeller().getId())
                    .sellerName(product.getSeller().getName())
                    .build();
        }
    }

    // 상품 조회 응답 (통계 정보 포함)
    @Builder
    @Schema(description = "상품 상세 조회 응답")
    public record ProductDetailResponse(
            @Schema(description = "상품 기본 정보")
            ProductBaseResponse product,

            @Schema(description = "상품 통계 정보")
            ProductStatsResponse stats
    ) {
        public static ProductDetailResponse from(
                Product product,
                Long viewCount,
                Long wishlistCount,
                boolean isWishlisted) {

            return ProductDetailResponse.builder()
                    .product(ProductBaseResponse.from(product))
                    .stats(new ProductStatsResponse(viewCount, wishlistCount, isWishlisted))
                    .build();
        }
    }

    // 상품 통계 정보
    @Builder
    @Schema(description = "상품 통계 정보")
    public record ProductStatsResponse(
            @Schema(description = "조회수", example = "42")
            Long viewCount,

            @Schema(description = "관심 등록 수", example = "7")
            Long wishlistCount,

            @Schema(description = "현재 사용자의 관심 등록 여부", example = "true")
            boolean isWishlisted
    ) {}
}
