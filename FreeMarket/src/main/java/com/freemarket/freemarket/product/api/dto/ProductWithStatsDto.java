package com.freemarket.freemarket.product.api.dto;

import com.freemarket.freemarket.product.domain.Product;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상품과 관련 통계 정보 포함 DTO")
public record ProductWithStatsDto(
        @Schema(description = "상품 정보", required = true)
        Product product,

        @Schema(description = "상품 조회수", example = "42")
        Long viewCount,

        @Schema(description = "관심 등록 수", example = "7")
        Long wishlistCount,

        @Schema(description = "현재 사용자의 관심 등록 여부", example = "true")
        boolean isWishlisted
) {

    public ProductWithStatsDto {
        // null 체크 및 기본값 설정
        viewCount = viewCount != null ? viewCount : 0L;
        wishlistCount = wishlistCount != null ? wishlistCount : 0L;
    }

    public ProductDto.ProductDetailResponse toProductDetailResponse() {
        return ProductDto.ProductDetailResponse.from(
                product,
                viewCount,
                wishlistCount,
                isWishlisted
        );
    }
}
