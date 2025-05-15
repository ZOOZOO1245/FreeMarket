package com.freemarket.freemarket.product.application;

import com.freemarket.freemarket.product.api.dto.ProductDto;
import com.freemarket.freemarket.product.domain.Product;
import com.freemarket.freemarket.product.domain.ProductStatus;
import com.freemarket.freemarket.product.exception.ProductException;
import com.freemarket.freemarket.review.domain.ReviewRepository;
import com.freemarket.freemarket.user.domain.User;
import com.freemarket.freemarket.user.domain.UserRepository;
import com.freemarket.freemarket.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductStatusService {

    private final ProductManagementService productManagementService;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    // 판매 완료 처리
    @Transactional
    public ProductDto.ProductBaseResponse markProductAsSold(Long sellerId, Long productId, Long buyerId) {
        Product product = productManagementService.getProductWithSellerCheck(productId, sellerId);

        if (product.getStatus() == ProductStatus.SOLD_OUT) {
            throw new ProductException.AlreadySoldProductException();
        }

        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new UserException.UserNotFoundException(buyerId));

        product.markAsSold(buyer);

        log.info("상품 판매완료 처리: 상품 ID {}, 판매자 ID {}, 구매자 ID {}", productId, sellerId, buyerId);

        return ProductDto.ProductBaseResponse.from(product);
    }

    // 판매완료 취소 처리 메서드
    @Transactional
    public ProductDto.ProductBaseResponse cancelProductSold(Long sellerId, Long productId) {
        Product product = productManagementService.getProductWithSellerCheck(productId, sellerId);

        if (product.getStatus() != ProductStatus.SOLD_OUT) {
            throw new ProductException.NotSoldProductException();
        }

        // 리뷰가 작성되었는지 확인
        boolean reviewExists = reviewRepository.findByProduct(product).isPresent();
        if (reviewExists) {
            throw new ProductException.CannotCancelSoldProductException("이미 리뷰가 작성된 상품은 판매완료 취소가 불가능합니다.");
        }

        product.cancelSold();
        log.info("판매완료 취소 처리: 상품 ID {}, 판매자 ID {}", productId, sellerId);
        return ProductDto.ProductBaseResponse.from(product);
    }
}
