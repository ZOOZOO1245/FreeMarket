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

    @Builder
    public User(String email, String password, String name, String phone, boolean enabled, UserRole role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.enabled = enabled;
        this.role = role != null ? role : UserRole.ROLE_USER;
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
}
