package com.freemarket.freemarket.global.oauth2.api.dto;

import com.freemarket.freemarket.global.oauth2.exception.OAuth2Exception;
import com.freemarket.freemarket.user.domain.User;
import com.freemarket.freemarket.user.domain.UserRole;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Builder
public record OAuthAttributes(
        Map<String, Object> attributes, // 원본 사용자 정보 속성
        String nameAttributeKey,        // 사용자 이름 속성 키 (설정 파일의 user-name-attribute 값)
        String name,
        String email,
        String provider,                // 예: "google", "kakao", "naver"
        String providerId               // 소셜 플랫폼 고유 ID
) {

    // registrationId(provider)에 따라 분기하여 OAuthAttributes 객체 생성
    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        log.debug("OAuthAttributes.of() called with registrationId: {}, userNameAttributeName: {}, attributes: {}",
                registrationId, userNameAttributeName, attributes);
        return switch (registrationId.toLowerCase()) {
            case "google" -> ofGoogle(userNameAttributeName, attributes);
            case "kakao" -> ofKakao(userNameAttributeName, attributes);
            case "naver" -> ofNaver("response", attributes); // 네이버는 user-name-attribute가 response 고정
            default -> throw new IllegalArgumentException("지원하지 않는 소셜 로그인입니다: " + registrationId);
        };
    }

    // Google 사용자 정보 추출
    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .provider("google")
                .providerId((String) attributes.get(userNameAttributeName)) // "sub"
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    // Kakao 사용자 정보 추출
    @SuppressWarnings("unchecked") // Map 캐스팅 경고 무시
    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        log.debug("카카오 응답 속성: {}", attributes);

        // OIDC 방식일 때는 다른 구조를 가질 수 있음
        String name = null;
        String email = null;
        String providerId = null;

        // ID 값 가져오기 (OIDC와 일반 OAuth2 모두 처리)
        if (attributes.containsKey(userNameAttributeName)) {
            providerId = String.valueOf(attributes.get(userNameAttributeName));
        }

        // 일반 OAuth2 응답 구조 처리
        if (attributes.containsKey("kakao_account")) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

            if (kakaoAccount != null) {
                email = (String) kakaoAccount.get("email");

                if (kakaoAccount.containsKey("profile")) {
                    Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                    if (profile != null) {
                        name = (String) profile.get("nickname");
                    }
                }
            }
        }
        // OIDC 응답 구조 처리
        else if (attributes.containsKey("email")) {
            email = (String) attributes.get("email");

            // OIDC에서는 name이 다른 위치에 있을 수 있음
            if (attributes.containsKey("name")) {
                name = (String) attributes.get("name");
            } else if (attributes.containsKey("nickname")) {
                name = (String) attributes.get("nickname");
            }
        }

        // 이름이 여전히 null이면 기본값 설정
        if (name == null) {
            name = "카카오 사용자";
        }

        return OAuthAttributes.builder()
                .name(name)
                .email(email)
                .provider("kakao")
                .providerId(providerId)
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    // Naver 사용자 정보 추출
    @SuppressWarnings("unchecked")
    private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        log.debug("네이버 속성: {}", attributes);

        // 응답에 'response' 객체가 있는지 확인
        Map<String, Object> responseData;
        if (attributes.containsKey("response")) {
            // 기존 구조: response 객체 내부에 데이터가 있는 경우
            responseData = (Map<String, Object>) attributes.get("response");
        } else {
            // 새로운 구조: 데이터가 최상위 레벨에 있는 경우
            responseData = attributes;
        }

        // 필수 필드 확인
        if (responseData == null || !responseData.containsKey("id")) {
            log.error("네이버 응답에서 사용자 정보를 찾을 수 없습니다. attributes: {}", attributes);
            throw new OAuth2Exception.MissingAttributeException("naver", "id");
        }

        String id = String.valueOf(responseData.get("id"));
        String name = (String) responseData.getOrDefault("name", "네이버 사용자");
        String email = (String) responseData.getOrDefault("email", id + "@naver.com");

        return OAuthAttributes.builder()
                .name(name)
                .email(email)
                .provider("naver")
                .providerId(id)
                .attributes(responseData)
                .nameAttributeKey("id")
                .build();
    }

    // OAuthAttributes 정보를 바탕으로 User 엔티티 생성 (최초 가입 시)
    public User toEntity() {
        return User.builder()
                .name(name)
                .email(email) // 이메일 동의 안 할 경우 null일 수 있으므로 예외처리 또는 대체값 필요
                .role(UserRole.ROLE_USER) // 기본 역할
                .enabled(true)
                .password(UUID.randomUUID().toString()) // 소셜 로그인은 비밀번호 불필요, 임의 값 설정
                .provider(provider)
                .providerId(providerId)
                .build();
    }
}
