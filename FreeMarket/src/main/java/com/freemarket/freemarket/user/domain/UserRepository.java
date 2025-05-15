package com.freemarket.freemarket.user.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // 소셜 로그인용 조회 메서드
    Optional<User> findByProviderAndProviderId(String provider, String providerId);
}
