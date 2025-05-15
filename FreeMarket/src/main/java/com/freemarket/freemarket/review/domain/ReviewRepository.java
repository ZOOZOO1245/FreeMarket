package com.freemarket.freemarket.review.domain;

import com.freemarket.freemarket.product.domain.Product;
import com.freemarket.freemarket.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {

    Optional<Review> findByProduct(Product product);

    // 특정 사용자(판매자)에 대한 리뷰 목록 페이징 조회
    Page<Review> findByTargetUserOrderByCreatedDateDesc(User targetUser, Pageable pageable);

    // 특정 사용자(구매자)가 작성한 리뷰 목록 페이징 조회
    Page<Review> findByReviewerOrderByCreatedDateDesc(User reviewer, Pageable pageable);

    // 특정 사용자가 받은 리뷰 수 조회
    long countByTargetUserId(Long targetUserId);
}
