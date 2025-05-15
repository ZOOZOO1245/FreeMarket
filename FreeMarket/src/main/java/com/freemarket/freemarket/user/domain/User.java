package com.freemarket.freemarket.user.domain;

import com.freemarket.freemarket.global.auditing.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(updatable = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    private String phone;

    @Column(nullable = false)
    private boolean enabled = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.ROLE_USER;

    // 소셜 로그인 정보 추가
    private String provider;     // 예: "naver", "google", "kakao"
    private String providerId;   // 소셜 플랫폼에서 제공하는 고유 사용자 ID

    @Column(nullable = false)
    private boolean emailVerified = false;

    @Builder
    public User(String email, String password, String name, String phone, boolean enabled, UserRole role, String provider, String providerId) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.enabled = enabled;
        this.role = role != null ? role : UserRole.ROLE_USER;
        this.provider = provider;
        this.providerId = providerId;
    }

    // 사용자 정보 업데이트 (소셜 로그인 시 이름 업데이트 등)
    public User updateOAuthInfo(String name) {
        this.name = name;
        return this;
    }

    // 사용자 정보 업데이트
    public void updateProfile(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    // 비밀번호 변경
    public void changePassword(String password) {
        this.password = password;
    }

    // 계정 활성화 메서드
    public void activateAccount() {
        this.enabled = true;
    }

    // 계정 비활성화 메서드
    public void deactivateAccount() {
        this.enabled = false;
    }

    // 사용자 역할 변경
    public void changeRole(UserRole role) {
        this.role = role;
    }

    public void updateProvider(String provider, String providerId) {
        this.provider = provider;
        this.providerId = providerId;
    }

    // 이메일 인증 상태 설정
    public void changeEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
}
