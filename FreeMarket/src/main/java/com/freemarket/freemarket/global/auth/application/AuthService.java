package com.freemarket.freemarket.global.auth.application;

import com.freemarket.freemarket.global.auth.api.dto.AuthDto;
import com.freemarket.freemarket.global.auth.domain.refresh.RefreshToken;
import com.freemarket.freemarket.global.jwt.JwtProvider;
import com.freemarket.freemarket.global.security.CustomUserDetails;
import com.freemarket.freemarket.user.domain.User;
import com.freemarket.freemarket.user.domain.UserRepository;
import com.freemarket.freemarket.user.domain.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public void signup(AuthDto.SignupRequest request) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 사용자 생성
        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .phone(request.phone())
                .role(UserRole.ROLE_USER)
                .enabled(true)
                .build();

        userRepository.save(user);
    }

    @Transactional
    public AuthDto.TokenResponse login(AuthDto.LoginRequest request) {
        try {
            // 인증 시도
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );

            // 인증 성공 시 SecurityContext에 인증 정보 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // JWT 토큰 생성
            String accessToken = jwtProvider.createAccessToken(authentication);
            String refreshToken = jwtProvider.createRefreshToken(authentication);

            // 사용자 ID 추출
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long userId = userDetails.getUserId();

            // 리프레시 토큰 저장
            refreshTokenService.saveRefreshToken(refreshToken, userId);

            log.info("로그인 성공: {}", request.email());

            return AuthDto.TokenResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtProvider.getAccessTokenValidityInSeconds())
                    .build();
        } catch (Exception e) {
            log.warn("로그인 실패: {}, 원인: {}", request.email(), e.getMessage());
            throw e;
        }
    }


    @Transactional
    public AuthDto.TokenResponse refreshToken(AuthDto.TokenRefreshRequest request) {
        log.info("토큰 갱신 요청");

        // RefreshToken 검증
        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(request.refreshToken());
        Long userId = refreshToken.getUserId();

        // 사용자 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 정보가 존재하지 않습니다."));

        // 새로운 인증 객체 생성
        CustomUserDetails userDetails = new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getRole().name(),
                user.isEnabled()
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // 새로운 토큰 생성
        String newAccessToken = jwtProvider.createAccessToken(authentication);
        String newRefreshToken = jwtProvider.createRefreshToken(authentication);

        // RefreshToken 업데이트
        refreshTokenService.saveRefreshToken(newRefreshToken, userId);

        log.info("토큰 갱신 성공: 사용자 ID {}", userId);

        return AuthDto.TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtProvider.getAccessTokenValidityInSeconds())
                .build();
    }

    @Transactional
    public void logout(Long userId) {
        log.info("로그아웃 요청: 사용자 ID {}", userId);
        refreshTokenService.deleteByUserId(userId);
        log.info("로그아웃 성공: 사용자 ID {}", userId);
    }
}
