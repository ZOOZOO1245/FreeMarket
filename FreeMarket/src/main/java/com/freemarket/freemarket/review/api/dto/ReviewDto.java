package com.freemarket.freemarket.review.api.dto;

import com.freemarket.freemarket.review.domain.Review;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ReviewDto {

    @Schema(description = "리뷰 작성 요청")
    public record ReviewCreateRequest(
        @Schema(description = "상품 ID", example = "123")
        @NotNull(message = "상품 ID는 필수 입력값입니다.")
        Long productId,

        @Schema(description = "별점 (1-5)", example = "4")
        @NotNull(message = "별점은 필수 입력값입니다.")
        @Min(value = 1, message = "별점은 1점 이상이어야 합니다.")
        @Max(value = 5, message = "별점은 5점 이하여야 합니다.")
        Integer rating,

        @Schema(description = "리뷰 내용", example = "친절하고 빠른 거래였습니다.")
        String content
    ) {}

    @Schema(description = "리뷰 수정 요청")
    public record ReviewUpdateRequest(
            @Schema(description = "별점 (1-5)", example = "5")
            @NotNull(message = "별점은 필수 입력값입니다.")
            @Min(value = 1, message = "별점은 1점 이상이어야 합니다.")
            @Max(value = 5, message = "별점은 5점 이하여야 합니다.")
            Integer rating,

            @Schema(description = "리뷰 내용", example = "정말 만족스러운 거래였습니다.")
            String content
    ) {}

    @Builder
    @Schema(description = "리뷰 응답")
    public record ReviewResponse(
            @Schema(description = "리뷰 ID", example = "1")
            Long id,

            @Schema(description = "리뷰어 ID (구매자)", example = "100")
            Long reviewerId,

            @Schema(description = "리뷰어 이름", example = "홍길동")
            String reviewerName,

            @Schema(description = "대상자 ID (판매자)", example = "200")
            Long targetUserId,

            @Schema(description = "대상자 이름", example = "김판매")
            String targetUserName,

            @Schema(description = "상품 ID", example = "123")
            Long productId,

            @Schema(description = "상품명", example = "중고 노트북")
            String productName,

            @Schema(description = "별점 (1-5)", example = "4")
            Integer rating,

            @Schema(description = "리뷰 내용", example = "친절하고 빠른 거래였습니다.")
            String content,

            @Schema(description = "작성일", example = "2023-09-15T14:30:00")
            LocalDateTime createdDate
    ) {
        public static ReviewResponse from(Review review) {
            return ReviewResponse.builder()
                    .id(review.getId())
                    .reviewerId(review.getReviewer().getId())
                    .reviewerName(review.getReviewer().getName())
                    .targetUserId(review.getTargetUser().getId())
                    .targetUserName(review.getTargetUser().getName())
                    .productId(review.getProduct().getId())
                    .productName(review.getProduct().getName())
                    .rating(review.getRating())
                    .content(review.getContent())
                    .createdDate(review.getCreatedDate())
                    .build();
        }
    }

    @Builder
    @Schema(description = "리뷰 통계 응답")
    public record ReviewStatsResponse (
            @Schema(description = "평균 평점", example = "4.3")
            double averageRating,

            @Schema(description = "총 리뷰 수", example = "27")
            long totalReviews,

            @Schema(description = "별점 분포 (각 별점별 개수)", example = "{\"1\": 2, \"2\": 3, \"3\": 5, \"4\": 10, \"5\": 7}")
            Map<Integer, Long> ratingDistribution,

            @Schema(description = "별점 분포 퍼센트", example = "{\"1\": 7.4, \"2\": 11.1, \"3\": 18.5, \"4\": 37.0, \"5\": 25.9}")
            Map<Integer, Double> ratingPercentages
    ) {}

    @Builder
    @Schema(description = "리뷰 목록 응답")
    public record ReviewListResponse(
            @Schema(description = "리뷰 목록")
            java.util.List<ReviewResponse> reviews,

            @Schema(description = "리뷰 통계")
            ReviewStatsResponse stats,

            @Schema(description = "총 페이지 수", example = "3")
            int totalPages,

            @Schema(description = "총 리뷰 수", example = "27")
            long totalElements
    ) {}

    @Builder
    @Schema(description = "내가 작성한 리뷰 목록 응답")
    public record MyReviewListResponse(
            @Schema(description = "리뷰 목록")
            List<ReviewResponse> reviews,

            @Schema(description = "총 페이지 수", example = "3")
            int totalPages,

            @Schema(description = "총 리뷰 수", example = "27")
            long totalElements
    ) {}
}
