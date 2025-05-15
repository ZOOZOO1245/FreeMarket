package com.freemarket.freemarket.review.domain;

import lombok.Getter;

import java.util.Map;

@Getter
public class RatingDistribution {
    private final Map<Integer, Long> distribution;
    private final long totalCount;

    public RatingDistribution(Map<Integer, Long> distribution) {
        this.distribution = distribution;
        this.totalCount = distribution.values().stream().mapToLong(Long::valueOf).sum();
    }

    public double getPercentage(int rating) {
        if (totalCount == 0) return 0.0;
        return (distribution.getOrDefault(rating, 0L) * 100.0 / totalCount);
    }
}
