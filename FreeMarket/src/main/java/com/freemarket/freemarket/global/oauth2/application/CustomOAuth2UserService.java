package com.freemarket.freemarket.global.oauth2.application;

import com.freemarket.freemarket.global.oauth2.api.dto.OAuthAttributes;
import com.freemarket.freemarket.user.domain.User;
import com.freemarket.freemarket.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    // naver용

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.debug("CustomOAuth2UserService: Loading user for request: {}", userRequest.getClientRegistration().getRegistrationId());

        // 기본 OAuth2UserService를 통해 사용자 정보 가져오기
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 현재 로그인 진행 중인 서비스 구분 (naver, google 등)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        // OAuth2 로그인 시 키가 되는 필드값 (application.yml의 user-name-attribute)
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        // OAuth2UserService를 통해 가져온 OAuth2User의 attribute를 담을 DTO
        Map<String, Object> attributesMap = oAuth2User.getAttributes();
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, attributesMap);

        // 사용자 정보 저장 또는 업데이트
        User user = saveOrUpdate(attributes);

        // OAuth2User 구현체 반환 (세션 대신 SecurityContext에 저장되어 후속 처리됨)
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().name())), // 사용자 권한
                attributes.attributes(), // 사용자 정보 속성
                attributes.nameAttributeKey()); // 사용자 이름 속성 키
    }

    // 사용자 정보 기반으로 회원가입 또는 정보 업데이트
    private User saveOrUpdate(OAuthAttributes attributes) {
        Optional<User> userOptional = userRepository.findByProviderAndProviderId(attributes.provider(), attributes.providerId());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.updateOAuthInfo(attributes.name());
            log.info("기존 사용자 로그인: provider={}, providerId={}, email={}",
                    attributes.provider(), attributes.providerId(), user.getEmail());
            return user;
        } else {
            // 이메일로 사용자 찾기 시도
            Optional<User> emailUser = userRepository.findByEmail(attributes.email());
            if (emailUser.isPresent()) {
                User user = emailUser.get();
                // provider 정보 업데이트
                user.updateProvider(attributes.provider(), attributes.providerId());
                log.info("기존 이메일 사용자에 소셜 연결: provider={}, email={}",
                        attributes.provider(), user.getEmail());
                return user;
            }

            // 새 사용자 생성
            User newUser = attributes.toEntity();
            userRepository.save(newUser);
            log.info("새 사용자 가입: provider={}, email={}",
                    attributes.provider(), newUser.getEmail());
            return newUser;
        }
    }
}
