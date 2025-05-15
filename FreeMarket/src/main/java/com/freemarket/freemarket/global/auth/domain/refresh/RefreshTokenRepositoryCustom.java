package com.freemarket.freemarket.global.auth.domain.refresh;

import java.time.LocalDateTime;

public interface RefreshTokenRepositoryCustom {
    void deleteExpiredTokens(LocalDateTime now); // 만료된 토큰 삭제
}
