package com.freemarket.freemarket.user.application;

import com.freemarket.freemarket.product.domain.Product;
import com.freemarket.freemarket.product.domain.ProductRepository;
import com.freemarket.freemarket.product.domain.ProductStatus;
import com.freemarket.freemarket.review.application.ReviewService;
import com.freemarket.freemarket.user.api.dto.UserProfileDto;
import com.freemarket.freemarket.user.domain.User;
import com.freemarket.freemarket.user.domain.UserRepository;
import com.freemarket.freemarket.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ReviewService reviewService;

    // 사용자의 프로필 요약 정보를 조회
    public UserProfileDto.ProfileSummaryResponse getProfileSummary(Long userId) {
        User user = getUser(userId);

        // 판매 중인 상품 수
        long activeProductCount = productRepository.countBySellerIdAndStatus(userId, ProductStatus.ACTIVE);

        // 판매 완료된 상품 수
        long soldProductCount = productRepository.countBySellerIdAndStatus(userId, ProductStatus.SOLD_OUT);

        // 구매한 상품 수
        long purchaseCount = productRepository.countByBuyerId(userId);

        // 평균 평점 조회
        double averageRating = reviewService.getUserAverageRating(userId);

        return UserProfileDto.ProfileSummaryResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .totalSellingCount((int) activeProductCount)
                .totalSoldCount((int) soldProductCount)
                .totalPurchaseCount((int) purchaseCount)
                .averageRating(averageRating)
                .joinDate(user.getCreatedDate())
                .build();
    }

    // 사용자의 판매 상품 내역을 조회
    public UserProfileDto.SellingHistoryResponse getSellingHistory(Long userId, Pageable pageable) {
        // 판매중인 상품 조회
        Page<Product> activeProductsPage = productRepository.findBySellerIdAndStatus(userId, ProductStatus.ACTIVE, pageable);

        // 판매 완료된 상품 조회
        Page<Product> soldProductsPage = productRepository.findBySellerIdAndStatus(userId, ProductStatus.SOLD_OUT, pageable);

        // DTO 변환
        List<UserProfileDto.SellingProductResponse> activeProductResponses =
                activeProductsPage.stream().map(this::mapToSellingProductResponse).collect(Collectors.toList());

        List<UserProfileDto.SellingProductResponse> soldProductResponses =
                soldProductsPage.stream().map(this::mapToSellingProductResponse).collect(Collectors.toList());

        return UserProfileDto.SellingHistoryResponse.builder()
                .activeProducts(activeProductResponses)
                .soldProducts(soldProductResponses)
                .totalProductCount((int) (activeProductsPage.getTotalElements() +
                        soldProductsPage.getTotalElements()))
                .build();

    }

    // 사용자의 구매 내역 조회 (Product 엔터티의 buyer 필드 기준)
    public UserProfileDto.PurchaseHistoryResponse getPurchaseHistory(Long userId, Pageable pageable) {
        // 사용자가 구매한 상품 목록 조회 (buyer_id 기준)
        Page<Product> purchasedProductsPage = productRepository.findByBuyerIdOrderBySoldDateDesc(userId, pageable);

        // DTO 변환
        List<UserProfileDto.PurchaseItem> purchaseItems = purchasedProductsPage.getContent().stream()
                .map(this::mapToPurchaseItem)
                .collect(Collectors.toList());

        return UserProfileDto.PurchaseHistoryResponse.builder()
                .purchases(purchaseItems)
                .totalPurchaseCount((int) purchasedProductsPage.getTotalElements())
                .build();
    }

    // Product 엔터티를 SellingProductResponse DTO로 변환
    private UserProfileDto.SellingProductResponse mapToSellingProductResponse(Product product) {
        String buyerName = product.getBuyer() != null ? product.getBuyer().getName() : null;
        return UserProfileDto.SellingProductResponse.builder()
                .productId(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .category(product.getCategory().getDisplayName())
                .status(product.getStatus().getDisplayName())
                .thumbnailUrl(product.getRepresentativeThumbnailUrl())
                // .viewCount(0) // TODO: 조회수 구현 시 추가
                // .likeCount(0) // TODO: 찜하기 기능 구현 시 추가
                .buyerName(buyerName)
                .soldDate(product.getSoldDate())
                .createdDate(product.getCreatedDate())
                .build();
    }

    // 구매한 상품을 PurchaseHistoryItem DTO로 변환
    private UserProfileDto.PurchaseItem mapToPurchaseItem(Product product) {
        return UserProfileDto.PurchaseItem.builder()
                .productId(product.getId())
                .productName(product.getName())
                .price(product.getPrice())
                .category(product.getCategory().getDisplayName())
                .thumbnailUrl(product.getRepresentativeThumbnailUrl())
                .sellerName(product.getSeller().getName())
                .purchaseDate(product.getSoldDate())
                .build();
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException.UserNotFoundException(userId));
    }
}
