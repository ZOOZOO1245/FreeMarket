package com.freemarket.freemarket.global.oauth2.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freemarket.freemarket.global.auth.api.dto.AuthDto;
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
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login Success Handler: Authentication successful.");
        clearAuthenticationAttributes(request);

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributesMap = oAuth2User.getAttributes();
        String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();

        log.debug("소셜 로그인 - provider: {}, attributes: {}", registrationId, attributesMap);

        // 네이버 로그인 처리
        if ("naver".equals(registrationId)) {
            try {
                // 응답 구조 확인
                Map<String, Object> userData;
                if (attributesMap.containsKey("response")) {
                    userData = (Map<String, Object>) attributesMap.get("response");
                } else {
                    userData = attributesMap; // 최상위 레벨에 데이터가 있는 경우
                }

                // 필수 정보 확인
                if (userData == null || !userData.containsKey("id")) {
                    log.error("네이버 응답에서 사용자 ID를 찾을 수 없습니다");
                    sendErrorResponse(response, "네이버 로그인 처리 중 오류가 발생했습니다");
                    return;
                }

                String providerId = String.valueOf(userData.get("id"));
                String email = (String) userData.get("email");
                String name = (String) userData.get("name");

                // providerId로 사용자 조회
                Optional<User> existingUser = userRepository.findByProviderAndProviderId("naver", providerId);

                User user;
                if (existingUser.isPresent()) {
                    // 기존 사용자 - 정보 업데이트
                    user = existingUser.get();
                    if (name != null) {
                        user.updateOAuthInfo(name);
                    }
                    log.info("기존 네이버 사용자 로그인: providerId={}", providerId);
                } else {
                    // 신규 사용자 - 회원가입
                    String userEmail = email != null ? email : providerId + "@naver.com";
                    user = User.builder()
                            .name(name != null ? name : "네이버 사용자")
                            .email(userEmail)
                            .password(UUID.randomUUID().toString())
                            .role(UserRole.ROLE_USER)
                            .enabled(true)
                            .provider("naver")
                            .providerId(providerId)
                            .build();
                    userRepository.save(user);
                    log.info("새 네이버 사용자 등록: providerId={}", providerId);
                }

                // JWT 토큰 발급 및 응답
                sendTokenResponse(user, response);
                return;
            } catch (Exception e) {
                log.error("네이버 로그인 처리 중 오류: {}", e.getMessage(), e);
                sendErrorResponse(response, "네이버 로그인 처리 중 오류가 발생했습니다");
                return;
            }
        }

        // 카카오, 구글 등 다른 소셜 로그인 처리
        String userNameAttributeName = getUserNameAttributeName(registrationId);
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, attributesMap);

        // 사용자 찾기 또는 생성
        User user = findOrCreateUser(attributes);

        // JWT 토큰 발급 및 응답
        sendTokenResponse(user, response);
    }

    // 사용자 찾기 또는 생성 메서드
    private User findOrCreateUser(OAuthAttributes attributes) {
        // providerId로 사용자 찾기
        Optional<User> existingUser = userRepository.findByProviderAndProviderId(
                attributes.provider(), attributes.providerId());

        if (existingUser.isPresent()) {
            // 기존 사용자 발견 - 정보 업데이트
            User user = existingUser.get();
            user.updateOAuthInfo(attributes.name());
            return user;
        }

        // 이메일로 사용자 찾기
        if (attributes.email() != null) {
            Optional<User> userByEmail = userRepository.findByEmail(attributes.email());
            if (userByEmail.isPresent()) {
                // 이메일로 사용자 발견 - 프로바이더 정보 업데이트
                User user = userByEmail.get();
                user.updateProvider(attributes.provider(), attributes.providerId());
                return user;
            }
        }

        // 신규 사용자 등록
        User newUser = attributes.toEntity();
        userRepository.save(newUser);
        return newUser;
    }

    // 오류 응답 전송
    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(ResponseDTO.error(HttpServletResponse.SC_BAD_REQUEST, message)));
        response.getWriter().flush();
    }

    // 토큰 응답 전송
    private void sendTokenResponse(User user, HttpServletResponse response) throws IOException {
        CustomUserDetails userDetails = new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getRole().name(),
                user.isEnabled()
        );
        Authentication jwtAuthentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        String accessToken = jwtProvider.createAccessToken(jwtAuthentication);
        String refreshToken = jwtProvider.createRefreshToken(jwtAuthentication);

        refreshTokenService.saveRefreshToken(refreshToken, user.getId());

        AuthDto.TokenResponse tokenResponse = AuthDto.TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtProvider.getAccessTokenValidityInSeconds())
                .build();

        ResponseDTO<AuthDto.TokenResponse> responseDTO = ResponseDTO.success(
                tokenResponse, "소셜 로그인에 성공했습니다.");

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(responseDTO));
        response.getWriter().flush();
    }

    // registrationId 기반으로 userNameAttributeName 결정 (설정 파일 값과 일치해야 함)
    private String getUserNameAttributeName(String registrationId) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> "sub";
            case "kakao" -> "id";
            case "naver" -> "response"; // OAuthAttributes.ofNaver에서 실제 키는 'id'로 처리함
            default -> throw new IllegalArgumentException("Unsupported provider: " + registrationId);
        };
    }
}
