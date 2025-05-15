package com.freemarket.freemarket.global.auth.domain.email;

import com.freemarket.freemarket.global.auditing.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_verification")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class EmailVerification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String verificationCode;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private boolean verified;

    @Builder
    public EmailVerification(String email, String verificationCode, LocalDateTime expiryDate, boolean verified) {
        this.email = email;
        this.verificationCode = verificationCode;
        this.expiryDate = expiryDate;
        this.verified = false;
    }

    public void verify() {
        this.verified = true;
    }
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }
}
