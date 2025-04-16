package com.freemarket.freemarket.global.auth.domain;

import java.time.LocalDateTime;

public interface PasswordResetTokenRepositoryCustom {
    void deleteExpiredTokens(LocalDateTime now);
}
