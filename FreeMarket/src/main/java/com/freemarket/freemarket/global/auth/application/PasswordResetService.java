package com.freemarket.freemarket.global.auth.application;

import com.freemarket.freemarket.global.auth.api.dto.PasswordDto;
import com.freemarket.freemarket.global.auth.domain.password.PasswordResetToken;
import com.freemarket.freemarket.global.auth.domain.password.PasswordResetTokenRepository;
import com.freemarket.freemarket.global.email.EmailService;
import com.freemarket.freemarket.global.auth.exception.AuthException;
import com.freemarket.freemarket.user.exception.UserException;
import com.freemarket.freemarket.user.domain.User;
import com.freemarket.freemarket.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void requestPasswordReset(PasswordDto.PasswordResetRequest request) {
        String email = request.email();

        // 사용자 존재 여부 확인
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException.UserNotFoundException(email));

        // 기존 토큰이 있으면 삭제
        passwordResetTokenRepository.findByEmail(email)
                .ifPresent(passwordResetTokenRepository::delete);

        // 새 토큰 생성
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .email(email)
                .build();

        // 토큰이 제대로 생성되었는지 로그 확인
        System.out.println("Generated token: " + resetToken.getToken());

        passwordResetTokenRepository.save(resetToken);

        // 이메일 발송
        emailService.sendPasswordResetEmail(email, resetToken.getToken());

        log.info("비밀번호 재설정 요청 처리 완료: {}", email);
    }

    @Transactional
    public void resetPassword(PasswordDto.PasswordResetVerifyRequest request) {
        String token = request.token();
        String newPassword = request.newPassword();

        // 토큰 검증
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new AuthException.PasswordResetTokenNotFoundException());

        if (resetToken.isExpired()) {
            throw new AuthException.PasswordResetTokenExpiredException();
        }

        // 사용자 정보 조회
        User user = userRepository.findByEmail(resetToken.getEmail())
                .orElseThrow(() -> new UserException.UserNotFoundException(resetToken.getEmail()));

        // 비밀번호 변경
        user.changePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // 사용한 토큰 삭제
        passwordResetTokenRepository.delete(resetToken);

        log.info("비밀번호 재설정 완료: {}", resetToken.getEmail());
    }

    @Scheduled(cron = "0 */30 * * * *") // 30분마다 실행
    @Transactional
    public void cleanupExpiredTokens() {
        log.info("만료된 비밀번호 재설정 토큰 정리 시작");
        passwordResetTokenRepository.deleteExpiredTokens(LocalDateTime.now());
        log.info("만료된 비밀번호 재설정 토큰 정리 완료");
    }
}
