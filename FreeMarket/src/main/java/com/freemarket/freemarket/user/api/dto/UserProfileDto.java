package com.freemarket.freemarket.user.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class UserProfileDto {

    @Builder
    @Schema(description = "판매 상품 내역")
    public record SellingProductResponse(
            @Schema(description = "상품 ID", example = "1")
            Long productId,
            
            @Schema(description = "상품명", example = "새 노트북")
            String name,
            
            @Schema(description = "가격", example = "1000000")
            Long price,
            
            @Schema(description = "카테고리", example = "전자기기")
            String category,
            
            @Schema(description = "상품 상태", example = "판매중")
            String status,
            
            @Schema(description = "썸네일 URL", example = "https://example.com/thumbnail.jpg")
            String thumbnailUrl,
            
            @Schema(description = "구매자 이름", example = "김철수")
            String buyerName,
            
            @Schema(description = "판매 완료 날짜", example = "2025-04-21T12:00:00")
            LocalDateTime soldDate,
            
            @Schema(description = "상품 등록 날짜", example = "2025-04-20T10:00:00")
            LocalDateTime createdDate
    ) {}

    @Builder
    @Schema(description = "판매 내역 페이지 응답")
    public record SellingHistoryResponse(
        @Schema(description = "판매 중인 상품 목록")
        List<SellingProductResponse> activeProducts,
        
        @Schema(description = "판매 완료된 상품 목록")
        List<SellingProductResponse> soldProducts,
        
        @Schema(description = "총 상품 수", example = "10")
        int totalProductCount
    ) {}

    @Builder
    @Schema(description = "구매 내역 항목")
    public record PurchaseItem(
        @Schema(description = "상품 ID", example = "1")
        Long productId,
        
        @Schema(description = "상품명", example = "중고 책")
        String productName,
        
        @Schema(description = "구매 가격", example = "15000")
        Long price,
        
        @Schema(description = "카테고리", example = "도서")
        String category,
        
        @Schema(description = "썸네일 URL", example = "https://example.com/thumbnail.jpg")
        String thumbnailUrl,
        
        @Schema(description = "판매자 이름", example = "이영희")
        String sellerName,
        
        @Schema(description = "구매 날짜", example = "2025-04-20T14:30:00")
        LocalDateTime purchaseDate
    ) {}

    @Builder
    @Schema(description = "구매 내역 페이지 응답")
    public record PurchaseHistoryResponse(
            @Schema(description = "구매 내역 목록")
            List<PurchaseItem> purchases,
            
            @Schema(description = "총 구매 수", example = "5")
            int totalPurchaseCount
    ) {}

    @Builder
    @Schema(description = "프로필 요약 정보")
    public record ProfileSummaryResponse(
            @Schema(description = "사용자 ID", example = "1")
            Long userId,
            
            @Schema(description = "이메일", example = "user@example.com")
            String email,
            
            @Schema(description = "이름", example = "홍길동")
            String name,
            
            @Schema(description = "총 판매 중인 상품 수", example = "3")
            int totalSellingCount,
            
            @Schema(description = "총 판매 완료 수", example = "7")
            int totalSoldCount,
            
            @Schema(description = "총 구매 수", example = "15")
            int totalPurchaseCount,
            
            @Schema(description = "평균 평점", example = "4.5")
            double averageRating,
            
            @Schema(description = "가입 날짜", example = "2025-01-01T09:00:00")
            LocalDateTime joinDate
    ) {}
}
