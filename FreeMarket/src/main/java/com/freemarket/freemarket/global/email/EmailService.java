package com.freemarket.freemarket.global.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;
    // 프론트엔드 비밀번호 재설정 URL 주입
    @Value("${frontend.reset-password-url}")
    private String frontendResetPasswordUrl;
    @Async
    public void sendEmail(String to, String subject, String content) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true); // HTML 형식 활성화

            emailSender.send(message);
            log.info("이메일 전송 완료: {}", to);
        } catch (MessagingException e) {
            log.error("이메일 전송 실패: {}", e.getMessage(), e);
            throw new RuntimeException("이메일 전송에 실패했습니다.", e);
        }
    }

    public void sendPasswordResetEmail(String to, String resetToken) {
        String subject = "FreeMarket 비밀번호 재설정";
        String resetUrl = frontendResetPasswordUrl + "?token=" + resetToken;

        String content = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                    <h2>비밀번호 재설정</h2>
                    <p>안녕하세요. FreeMarket 비밀번호 재설정을 요청하셨습니다.</p>
                    <p>아래 링크를 클릭하여 비밀번호를 재설정해 주세요:</p>
                    <p><a href="%s" style="display: inline-block; padding: 10px 20px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 5px;">비밀번호 재설정</a></p>
                    <p>이 링크는 30분 동안만 유효합니다.</p>
                    <p>비밀번호 재설정을 요청하지 않으셨다면 이 이메일을 무시하셔도 됩니다.</p>
                    <p>감사합니다.<br>FreeMarket 팀</p>
                </div>
                """.formatted(resetUrl);

        sendEmail(to, subject, content);
    }
}
