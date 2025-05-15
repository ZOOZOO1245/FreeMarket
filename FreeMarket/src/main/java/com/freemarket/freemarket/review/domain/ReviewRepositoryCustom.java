package com.freemarket.freemarket.review.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewRepositoryCustom {

    // 특정 별점 이상의 리뷰만 조회
    Page<Review> findByTargetUserIdAndMinimumRating(Long targetUserId, Integer minimumRating, Pageable pageable);

    // 별점 분포 통계 조회
    RatingDistribution getRatingDistributionByUserId(Long userId);

    // 평균 평점 조회
    Double getAverageRatingByUserId(Long userId);
}
