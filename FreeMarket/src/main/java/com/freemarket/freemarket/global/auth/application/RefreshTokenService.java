package com.freemarket.freemarket.global.auth.application;

import com.freemarket.freemarket.global.auth.domain.refresh.RefreshToken;
import com.freemarket.freemarket.global.auth.domain.refresh.RefreshTokenRepository;
import com.freemarket.freemarket.global.auth.exception.AuthException;
import com.freemarket.freemarket.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public void saveRefreshToken(String token, Long userId) {
        LocalDateTime expiryDate = LocalDateTime.now()
                .plusSeconds(jwtProvider.getRefreshTokenValidityInSeconds());

        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(existing -> {
                    // 기존 토큰이 있으면 업데이트
                    existing.updateToken(token, expiryDate);
                    return existing;
                })
                .orElse(RefreshToken.builder()
                        .token(token)
                        .userId(userId)
                        .expiryDate(expiryDate)
                        .build());

        refreshTokenRepository.save(refreshToken);
        log.info("RefreshToken 저장 완료: 사용자 ID {}", userId);
    }

    public RefreshToken validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 RefreshToken");
                    return new AuthException.InvalidRefreshTokenException();
                });

        if (refreshToken.isExpired()) {
            log.warn("만료된 리프레시 토큰: 사용자 ID {}", refreshToken.getUserId());
            throw new AuthException.ExpiredRefreshTokenException();
        }

        return refreshToken;
    }


    @Transactional
    public void deleteByUserId(Long userId) {
        refreshTokenRepository.findByUserId(userId).ifPresent(token -> {
            refreshTokenRepository.delete(token);
            log.info("RefreshToken 삭제 완료: 사용자 ID {}", userId);
        });
    }

    @Transactional
    @Scheduled(cron = "0 0 */6 * * *") // 6시간마다 실행
    public void cleanupExpiredTokens() {
        log.info("만료된 리프레시 토큰 정리 시작");
        refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
        log.info("만료된 리프레시 토큰 정리 완료");
    }
}
