package com.freemarket.freemarket.review.domain;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.freemarket.freemarket.review.domain.QReview.review;

@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Review> findByTargetUserIdAndMinimumRating(Long targetUserId, Integer minimumRating, Pageable pageable) {
        List<Review> content = queryFactory
                .selectFrom(review)
                .where(review.targetUser.id.eq(targetUserId)
                        .and(review.rating.goe(minimumRating)))
                .orderBy(review.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(review.count())
                .from(review)
                .where(review.targetUser.id.eq(targetUserId)
                        .and(review.rating.goe(minimumRating)));

        return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
    }

    @Override
    public RatingDistribution getRatingDistributionByUserId(Long userId) {
        List<RatingCount> ratingCount = queryFactory
                .select(Projections.constructor(RatingCount.class,
                        review.rating,
                        review.count()))
                .from(review)
                .where(review.targetUser.id.eq(userId))
                .groupBy(review.rating)
                .fetch();

        Map<Integer, Long> distribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            distribution.put(i, 0L);
        }

        // 조회 결과로 맵 업데이트
        for (RatingCount count : ratingCount) {
            distribution.put(count.getRating(), count.getCount());
        }

        return new RatingDistribution(distribution);
    }

    @Override
    public Double getAverageRatingByUserId(Long userId) {
        Double avgRating = queryFactory
                .select(review.rating.avg())
                .from(review)
                .where(review.targetUser.id.eq(userId))
                .fetchOne();

        return avgRating != null ? avgRating : 0.0;
    }
}
