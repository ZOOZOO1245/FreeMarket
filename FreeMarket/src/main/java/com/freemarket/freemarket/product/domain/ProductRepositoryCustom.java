package com.freemarket.freemarket.product.domain;

import com.freemarket.freemarket.product.api.dto.ProductDto;
import com.freemarket.freemarket.product.api.dto.ProductWithStatsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {
    // 전체 상품 조회 (필터링 옵션 포함)
    Page<Product> findAllWithFilters(String keyword, ProductStatus status, Pageable pageable);

    // 카테고리별 상품 조회
    Page<Product> findByCategoryWithFilters(ProductCategory category, String keyword, ProductStatus status, Pageable pageable);

    // 통계 정보를 함께 조회
    Page<ProductWithStatsDto> findAllWithStatsAndWishlist(String keyword, ProductStatus status, Pageable pageable, Long userId);
    Page<ProductWithStatsDto> findByCategoryWithStatsAndWishlist(ProductCategory category, String keyword, ProductStatus status, Pageable pageable, Long userId);
}
