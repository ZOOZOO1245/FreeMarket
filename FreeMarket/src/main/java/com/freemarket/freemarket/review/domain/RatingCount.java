package com.freemarket.freemarket.review.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RatingCount {
    private final Integer rating;
    private final Long count;
}
