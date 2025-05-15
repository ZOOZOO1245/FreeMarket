package com.freemarket.freemarket.global.oauth2.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freemarket.freemarket.global.auth.application.RefreshTokenService;
import com.freemarket.freemarket.global.common.ResponseDTO;
import com.freemarket.freemarket.global.jwt.JwtProvider;
import com.freemarket.freemarket.global.oauth2.api.dto.OAuthAttributes;
import com.freemarket.freemarket.global.security.CustomUserDetails;
import com.freemarket.freemarket.user.domain.User;
import com.freemarket.freemarket.user.domain.UserRepository;
import com.freemarket.freemarket.user.domain.UserRole;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@Transactional // 트랜잭션 추가 (사용자 생성/업데이트, 리프레시 토큰 저장)
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper; // 오류 응답 전송 시 필요할 수 있음

    // 프론트엔드 콜백 URL 주입
    @Value("${frontend.oauth.callback-url}")
    private String frontendCallbackUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login Success Handler: Authentication successful.");
        clearAuthenticationAttributes(request); // 이전 인증 정보 클리어

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributesMap = oAuth2User.getAttributes();
        String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();

        log.debug("소셜 로그인 - provider: {}, attributes: {}", registrationId, attributesMap);

        User user;
        try {
            // 네이버 로그인 특별 처리 (응답 구조가 다를 수 있음)
            if ("naver".equals(registrationId)) {
                user = processNaverLogin(attributesMap);
            } else { // 구글, 카카오 등 다른 소셜 로그인 처리
                String userNameAttributeName = getUserNameAttributeName(registrationId);
                OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, attributesMap);
                user = findOrCreateUser(attributes);
            }
        } catch (Exception e) {
            log.error("소셜 로그인 사용자 처리 중 오류 발생: {}", e.getMessage(), e);
            // 오류 발생 시 사용자에게 알릴 오류 페이지로 리다이렉트하거나 오류 응답 전송
            sendErrorResponse(response, "소셜 로그인 처리 중 오류가 발생했습니다.");
            return;
        }


        // 사용자 정보 기반으로 JWT 생성 및 프론트엔드로 리다이렉션
        sendTokenViaRedirect(user, request, response);
    }

    // 네이버 로그인 처리 로직
    private User processNaverLogin(Map<String, Object> attributesMap) {
        Map<String, Object> responseData;
        if (attributesMap.containsKey("response")) {
            responseData = (Map<String, Object>) attributesMap.get("response");
        } else {
            responseData = attributesMap; // 최상위 레벨에 데이터가 있는 경우
        }

        if (responseData == null || !responseData.containsKey("id")) {
            log.error("네이버 응답에서 사용자 ID를 찾을 수 없습니다");
            throw new IllegalStateException("네이버 로그인 처리 중 필수 정보(ID) 누락");
        }

        String providerId = String.valueOf(responseData.get("id"));
        String email = (String) responseData.get("email");
        String name = (String) responseData.get("name");

        Optional<User> existingUser = userRepository.findByProviderAndProviderId("naver", providerId);

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (name != null) { // 이름 정보가 있으면 업데이트
                user.updateOAuthInfo(name);
            }
            log.info("기존 네이버 사용자 로그인: providerId={}", providerId);
            return user;
        } else {
            // 이메일 중복 체크 및 처리 (선택적이지만 권장)
            String userEmail = email != null ? email : providerId + "@naver.com"; // 이메일 없으면 임시 생성
            if (email != null && userRepository.existsByEmail(email)) {
                log.warn("네이버 로그인 시도: 이미 가입된 이메일 {}", email);
                // 이미 가입된 이메일에 네이버 계정 정보를 연결하거나, 에러 처리
                // 예: 기존 계정에 provider 정보 업데이트
                User userByEmail = userRepository.findByEmail(email)
                        .orElseThrow(() -> new IllegalStateException("이메일 존재하나 사용자 못찾음: " + email)); // 비정상 케이스
                userByEmail.updateProvider("naver", providerId);
                if (name != null) userByEmail.updateOAuthInfo(name);
                log.info("기존 이메일({}) 계정에 네이버 계정(ID:{}) 연결", email, providerId);
                return userByEmail;
                // 또는 throw new IllegalStateException("이미 가입된 이메일입니다: " + email);
            }

            // 신규 사용자 등록
            User newUser = User.builder()
                    .name(name != null ? name : "네이버 사용자") // 이름 없으면 기본값
                    .email(userEmail)
                    .password(UUID.randomUUID().toString()) // 비밀번호는 임의값 사용
                    .role(UserRole.ROLE_USER)
                    .enabled(true)
                    .provider("naver")
                    .providerId(providerId)
                    .build();
            userRepository.save(newUser);
            log.info("새 네이버 사용자 등록: providerId={}", providerId);
            return newUser;
        }
    }


    // 구글, 카카오 등 사용자 찾기 또는 생성 메서드
    private User findOrCreateUser(OAuthAttributes attributes) {
        Optional<User> existingUser = userRepository.findByProviderAndProviderId(
                attributes.provider(), attributes.providerId());

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.updateOAuthInfo(attributes.name()); // 이름 등 변경사항 업데이트
            log.info("기존 {} 사용자 로그인: providerId={}, email={}",
                    attributes.provider(), attributes.providerId(), user.getEmail());
            return user;
        }

        // 이메일로 기존 사용자 찾기 (다른 방식으로 가입했을 수 있음)
        if (attributes.email() != null) {
            Optional<User> userByEmail = userRepository.findByEmail(attributes.email());
            if (userByEmail.isPresent()) {
                User user = userByEmail.get();
                // 기존 계정에 소셜 로그인 정보 연결
                user.updateProvider(attributes.provider(), attributes.providerId());
                user.updateOAuthInfo(attributes.name()); // 이름 업데이트
                log.info("기존 이메일({}) 사용자에 {} 계정 연결: providerId={}",
                        user.getEmail(), attributes.provider(), attributes.providerId());
                return user;
            }
        }

        // 신규 사용자 생성
        User newUser = attributes.toEntity(); // OAuthAttributes에서 User 엔티티 생성
        userRepository.save(newUser);
        log.info("새 {} 사용자 등록: providerId={}, email={}",
                attributes.provider(), attributes.providerId(), newUser.getEmail());
        return newUser;
    }

    // JWT 토큰 생성 및 프론트엔드 콜백 URL로 리다이렉션
    private void sendTokenViaRedirect(User user, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 1. CustomUserDetails 생성 (JWT 페이로드에 넣을 정보 포함)
        CustomUserDetails userDetails = new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(), // 비밀번호는 JWT 생성 시 직접 사용되지는 않음
                user.getRole().name(),
                user.isEnabled()
        );

        // 2. Authentication 객체 생성 (JWT 생성용)
        Authentication jwtAuthentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        // 3. JWT 토큰 생성 (Access Token, Refresh Token)
        String accessToken = jwtProvider.createAccessToken(jwtAuthentication);
        String refreshToken = jwtProvider.createRefreshToken(jwtAuthentication);

        // 4. Refresh Token DB 저장/업데이트
        refreshTokenService.saveRefreshToken(refreshToken, user.getId());

        // 5. 토큰 URL 인코딩 (URL에 포함시키기 위함)
        String encodedAccessToken = URLEncoder.encode(accessToken, StandardCharsets.UTF_8);
        String encodedRefreshToken = URLEncoder.encode(refreshToken, StandardCharsets.UTF_8);

        // 6. 프론트엔드 콜백 URL 생성 (토큰 포함)
        String targetUrl = UriComponentsBuilder.fromUriString(frontendCallbackUrl)
                .queryParam("accessToken", encodedAccessToken)
                .queryParam("refreshToken", encodedRefreshToken)
                // 필요시 추가 정보 전달 (예: 최초 로그인 여부 등)
                // .queryParam("isNewUser", ...)
                .build().toUriString();

        // 7. 프론트엔드로 리다이렉션
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
        log.info("OAuth2 로그인 성공, 프론트엔드({})로 토큰과 함께 리다이렉션 완료.", targetUrl);
    }

    // registrationId 기반으로 userNameAttributeName 결정
    private String getUserNameAttributeName(String registrationId) {
        // OIDC 표준은 'sub'를 사용.
        return switch (registrationId.toLowerCase()) {
            case "google" -> "sub";
            case "kakao" -> "sub";
            case "naver" -> "response"; // 네이버는 고유 구조 사용 (실제 ID는 response 객체 내 'id')
            default -> throw new IllegalArgumentException("지원하지 않는 소셜 로그인입니다: " + registrationId);
        };
    }

    // 오류 발생 시 응답 전송
    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        // 간단한 오류 메시지만 전달하거나, ResponseDTO 사용
        ResponseDTO<Void> responseDTO = ResponseDTO.error(HttpServletResponse.SC_BAD_REQUEST, message);
        response.getWriter().write(objectMapper.writeValueAsString(responseDTO));
        response.getWriter().flush();
    }
}
