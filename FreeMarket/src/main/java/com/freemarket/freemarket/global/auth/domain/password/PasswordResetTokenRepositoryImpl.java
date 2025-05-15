package com.freemarket.freemarket.global.auth.domain.password;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import static com.freemarket.freemarket.global.auth.domain.password.QPasswordResetToken.passwordResetToken;


@RequiredArgsConstructor
public class PasswordResetTokenRepositoryImpl implements PasswordResetTokenRepositoryCustom {
    private final JPAQueryFactory queryFactory;


    @Override
    public void deleteExpiredTokens(LocalDateTime now) {
        queryFactory
                .delete(passwordResetToken)
                .where(passwordResetToken.expiryDate.lt(now))
                .execute();
    }
}
