package com.freemarket.freemarket.global.auth.application;

import com.freemarket.freemarket.global.auth.api.dto.EmailVerificationDto;
import com.freemarket.freemarket.global.auth.domain.email.EmailVerification;
import com.freemarket.freemarket.global.auth.domain.email.EmailVerificationRepository;
import com.freemarket.freemarket.global.email.exception.EmailVerificationException;
import com.freemarket.freemarket.user.domain.User;
import com.freemarket.freemarket.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationRepository emailVerificationRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    private static final String ALLOWED_DOMAINS = "office.skhu.ac.kr";
    private static final int CODE_LENGTH = 6;
    private static final int EXPIRY_MINUTES = 10;

    @Transactional
    public EmailVerificationDto.EmailVerificationResponse sendVerificationEmail(EmailVerificationDto.EmailVerificationRequest request) throws BadRequestException {
        String email = request.email();

        // 학교 이메일 도메인 확인
        if (!isSchoolEmail(email)) {
            throw new EmailVerificationException.InvalidEmailDomainException(email);
        }

        // 인증 코드 생성
        String verificationCode = generateVerificationCode();

        // 이전 인증 코드 만료 처리
        emailVerificationRepository.findByEmail(email)
                .ifPresent(emailVerificationRepository::delete);

        // 새 인증 코드 저장
        EmailVerification emailVerification = EmailVerification.builder()
                .email(email)
                .verificationCode(verificationCode)
                .expiryDate(LocalDateTime.now().plusMinutes(EXPIRY_MINUTES))
                .build();

        emailVerificationRepository.save(emailVerification);

        // 이메일 발송
        sendEmail(email, "이메일 인증 코드", "인증 코드: " + verificationCode + "\n\n이 코드는 " + EXPIRY_MINUTES + "분 후에 만료됩니다.");

        return new EmailVerificationDto.EmailVerificationResponse(true, "인증 이메일이 발송되었습니다.");
    }

    @Transactional
    public EmailVerificationDto.EmailVerificationResponse verifyEmail(String userEmail, EmailVerificationDto.VerificationCodeRequest request) throws BadRequestException {
        String email = request.email();
        String code = request.verificationCode();

        EmailVerification verification = emailVerificationRepository.findByEmail(email)
                .orElseThrow(() -> new EmailVerificationException.VerificationNotFoundException(email));

        if (verification.isExpired()) {
            throw new EmailVerificationException.VerificationCodeExpiredException();
        }

        if (!verification.getVerificationCode().equals(code)) {
            throw new EmailVerificationException.InvalidVerificationCodeException();
        }

        verification.verify();
        emailVerificationRepository.save(verification);

        // 사용자의 이메일 인증 상태 업데이트
        Optional<User> userOpt = userRepository.findByEmail(userEmail);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.changeEmailVerified(true);
            userRepository.save(user);
        }

        return new EmailVerificationDto.EmailVerificationResponse(true, "이메일 인증이 완료되었습니다.");
    }

    private boolean isSchoolEmail(String email) {
        return email.toLowerCase().endsWith("@" + ALLOWED_DOMAINS);
    }

    private String generateVerificationCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }

        return code.toString();
    }

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
