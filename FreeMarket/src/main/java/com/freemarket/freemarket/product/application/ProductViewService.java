package com.freemarket.freemarket.product.application;

import com.freemarket.freemarket.product.domain.Product;
import com.freemarket.freemarket.product.domain.ProductRepository;
import com.freemarket.freemarket.product.domain.ProductViewCount;
import com.freemarket.freemarket.product.domain.ProductViewCountRepository;
import com.freemarket.freemarket.product.exception.ProductException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductViewService {

    private final ProductRepository productRepository;
    private final ProductViewCountRepository viewCountRepository;

    @Transactional
    public void incrementViewCount(Long productId) {
        Product product = getProduct(productId);
        ProductViewCount viewCount = viewCountRepository.findByProductId(productId)
                .orElseGet(() -> {
                   ProductViewCount newViewCount = ProductViewCount.builder()
                           .product(product)
                           .build();
                   return viewCountRepository.save(newViewCount);
                });

        viewCount.incrementCount();
    }

    public Long getViewCount(Long productId) {
        return viewCountRepository.findByProductId(productId)
                .map(ProductViewCount::getCount)
                .orElse(0L);
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId).orElseThrow(() -> new ProductException.ProductNotFoundException(productId));
    }
}
