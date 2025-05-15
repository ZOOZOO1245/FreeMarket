package com.freemarket.freemarket.review.application;

import com.freemarket.freemarket.product.domain.Product;
import com.freemarket.freemarket.product.domain.ProductRepository;
import com.freemarket.freemarket.product.domain.ProductStatus;
import com.freemarket.freemarket.product.exception.ProductException;
import com.freemarket.freemarket.review.api.dto.ReviewDto;
import com.freemarket.freemarket.review.domain.RatingDistribution;
import com.freemarket.freemarket.review.domain.Review;
import com.freemarket.freemarket.review.domain.ReviewRepository;
import com.freemarket.freemarket.review.exception.ReviewException;
import com.freemarket.freemarket.user.domain.User;
import com.freemarket.freemarket.user.domain.UserRepository;
import com.freemarket.freemarket.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    // 리뷰 생성
    @Transactional
    public ReviewDto.ReviewResponse createReview(Long reviewerId, ReviewDto.ReviewCreateRequest request) {
        User reviewer = getUser(reviewerId);
        Product product = getProduct(request.productId());

        // 판매자가 리뷰 작성자와 같으면 예외 발생
        if (product.getSeller().getId().equals(reviewerId)) {
            throw new ReviewException.SelfReviewException();
        }

        // 상품이 판매완료 상태가 아니면 예외 발생
        if (product.getStatus() != ProductStatus.SOLD_OUT) {
            throw new ReviewException.ProductNotSoldException();
        }

        // 구매자가 리뷰 작성자와 같은지 확인
        if (product.getBuyer() == null || !product.getBuyer().getId().equals(reviewerId)) {
            throw new ReviewException.NotBuyerException();
        }

        // 이미 리뷰가 있는지 확인
        if (reviewRepository.findByProduct(product).isPresent()) {
            throw new ReviewException.ReviewAlreadyExistsException();
        }

        User targetUser = product.getSeller();

        Review review = Review.builder()
                .reviewer(reviewer)
                .targetUser(targetUser)
                .product(product)
                .rating(request.rating())
                .content(request.content())
                .build();

        Review savedReview = reviewRepository.save(review);
        log.info("리뷰 생성 완료: 리뷰어 ID {}, 판매자 ID {}, 상품 ID {}", reviewerId, targetUser.getId(), product.getId());

        return ReviewDto.ReviewResponse.from(review);
    }

    // 리뷰 수정
    @Transactional
    public ReviewDto.ReviewResponse updateReview(Long reviewerId, Long reviewId, ReviewDto.ReviewUpdateRequest request) {
        Review review = getReviewWithReviewerCheck(reviewId, reviewerId);
        review.update(request.rating(), request.content());

        log.info("리뷰 수정 완료: 리뷰 ID {}, 리뷰어 ID {}", reviewId, reviewerId);

        return ReviewDto.ReviewResponse.from(review);
    }

    // 리뷰 삭제
    @Transactional
    public void deleteReview(Long reviewerId, Long reviewId) {
        Review review = getReviewWithReviewerCheck(reviewId, reviewerId);
        reviewRepository.delete(review);

        log.info("리뷰 삭제 완료: 리뷰 ID {}, 리뷰어 ID {}", reviewId, reviewerId);
    }

    // 리뷰 조회
    public ReviewDto.ReviewResponse findReview(Long reviewId) {
        Review review = getReview(reviewId);
        return ReviewDto.ReviewResponse.from(review);
    }

    // 특정 사용자에 대한 리뷰 목록 조회 (판매자 대상)
    public ReviewDto.ReviewListResponse getUserReviews(Long targetUserId, Pageable pageable) {
        User targetUser = getUser(targetUserId);

        Page<Review> reviewsPage = reviewRepository.findByTargetUserOrderByCreatedDateDesc(targetUser, pageable);

        List<ReviewDto.ReviewResponse> reviews = reviewsPage.getContent().stream()
                .map(ReviewDto.ReviewResponse::from)
                .collect(Collectors.toList());

        // 평점 통계 조회
        Double averageRating = reviewRepository.getAverageRatingByUserId(targetUserId);
        if (averageRating == null) averageRating = 0.0;

        long totalReviews = reviewRepository.countByTargetUserId(targetUserId);

        // 별점 분포 조회
        RatingDistribution ratingDistribution = reviewRepository.getRatingDistributionByUserId(targetUserId);

        Map<Integer, Double> percentages = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            percentages.put(i, ratingDistribution.getPercentage(i));
        }

        ReviewDto.ReviewStatsResponse stats = ReviewDto.ReviewStatsResponse.builder()
                .averageRating(averageRating)
                .totalReviews(totalReviews)
                .ratingDistribution(ratingDistribution.getDistribution())
                .ratingPercentages(percentages)
                .build();

        return ReviewDto.ReviewListResponse.builder()
                .reviews(reviews)
                .stats(stats)
                .totalPages(reviewsPage.getTotalPages())
                .totalElements(reviewsPage.getTotalElements())
                .build();
    }

    // 자신이 작성한 리뷰 목록 조회
    public ReviewDto.MyReviewListResponse getMyReviews(Long reviewerId, Pageable pageable) {
        User reviewer = getUser(reviewerId);

        Page<Review> reviewsPage = reviewRepository.findByReviewerOrderByCreatedDateDesc(reviewer, pageable);

        List<ReviewDto.ReviewResponse> reviews = reviewsPage.getContent().stream()
                .map(ReviewDto.ReviewResponse::from)
                .collect(Collectors.toList());

        return ReviewDto.MyReviewListResponse.builder()
                .reviews(reviews)
                .totalPages(reviewsPage.getTotalPages())
                .totalElements(reviewsPage.getTotalElements())
                .build();
    }

    // 특정 사용자의 평균 평점 조회
    public double getUserAverageRating(Long userId) {
        Double averageRating = reviewRepository.getAverageRatingByUserId(userId);
        return averageRating != null ? averageRating : 0.0;
    }


    // 권한 확인 후 리뷰 조회
    private Review getReviewWithReviewerCheck(Long reviewId, Long reviewerId) {
        Review review = getReview(reviewId);

        if (!review.getReviewer().getId().equals(reviewerId)) {
            throw new ReviewException.ReviewAccessDeniedException();
        }

        return review;
    }

    private Review getReview(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException.ReviewNotFoundException(reviewId));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException.UserNotFoundException(userId));
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductException.ProductNotFoundException(productId));
    }
}
