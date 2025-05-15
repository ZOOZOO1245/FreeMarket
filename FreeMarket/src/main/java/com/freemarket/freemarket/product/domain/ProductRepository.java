package com.freemarket.freemarket.product.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {
    // 판매자 ID와 상품 상태로 상품 목록 조회
    Page<Product> findBySellerIdAndStatus(Long sellerId, ProductStatus status, Pageable pageable);

    // 판매자 ID와 상품 상태로 상품 개수 조회
    long countBySellerIdAndStatus(Long sellerId, ProductStatus status);

    // 구매자 ID로 상품 목록 조회
    Page<Product> findByBuyerIdOrderBySoldDateDesc(Long buyerId, Pageable pageable);

    // 구매자 ID로 상품 개수 조회
    long countByBuyerId(Long buyerId);
}
