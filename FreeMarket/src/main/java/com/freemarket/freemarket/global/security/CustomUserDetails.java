package com.freemarket.freemarket.global.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Long userId;
    private final String email;
    private final String password;
    private final String role;
    private final boolean enabled;

    public CustomUserDetails(Long userId, String email, String password, String role, boolean enabled) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.role = role;
        this.enabled = enabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // role 값에 "ROLE_" 접두사가 없으면 추가
        String authority = this.role.startsWith("ROLE_") ? this.role : "ROLE_" + this.role;
        return Collections.singleton(new SimpleGrantedAuthority(authority));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        // Spring Security의 username으로 email을 사용
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        // 계정 만료 여부: true는 만료되지 않음을 의미
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // 계정 잠금 여부: true는 잠기지 않음을 의미
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // 자격 증명 만료 여부: true는 만료되지 않음을 의미
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 계정 활성화 여부
        return this.enabled;
    }
}
