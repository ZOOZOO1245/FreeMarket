package com.freemarket.freemarket.global.auth.domain.refresh;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import static com.freemarket.freemarket.global.auth.domain.refresh.QRefreshToken.refreshToken;


@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public void deleteExpiredTokens(LocalDateTime now) {
        queryFactory
                .delete(refreshToken)
                .where(refreshToken.expiryDate.lt(now))
                .execute();
    }
}
