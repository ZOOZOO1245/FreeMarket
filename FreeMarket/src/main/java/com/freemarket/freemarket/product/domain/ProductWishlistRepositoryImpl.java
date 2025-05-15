package com.freemarket.freemarket.product.domain;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static com.freemarket.freemarket.product.domain.QProductWishlist.productWishlist;

@RequiredArgsConstructor
public class ProductWishlistRepositoryImpl implements ProductWishlistRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Long countByProductId(Long productId) {
        return queryFactory
                .select(productWishlist.count())
                .from(productWishlist)
                .where(productWishlist.product.id.eq(productId))
                .fetchOne();
    }
}
