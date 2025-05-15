package com.freemarket.freemarket.product.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductViewCountRepository extends JpaRepository<ProductViewCount, Long> {
    Optional<ProductViewCount> findByProductId(Long productId);
}
