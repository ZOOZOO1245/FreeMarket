package com.freemarket.freemarket.global.oauth2.application;

import com.freemarket.freemarket.global.oauth2.api.dto.OAuthAttributes;
import com.freemarket.freemarket.global.oauth2.exception.OAuth2Exception;
import com.freemarket.freemarket.user.domain.User;
import com.freemarket.freemarket.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService { // OidcUserService 상속

    private final UserRepository userRepository;
    // google, kakao용
    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        log.debug("CustomOidcUserService: Loading user for request: {}", userRequest.getClientRegistration().getRegistrationId());

        // 기본 OidcUserService를 통해 사용자 정보 가져오기 (ID Token 포함)
        OidcUser oidcUser = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        // OIDC는 ID Token의 'sub' 또는 설정된 user-name-attribute 사용
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        Map<String, Object> attributesMap = oidcUser.getAttributes();
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, attributesMap);

        User user = saveOrUpdate(attributes);

        // OidcUser 구현체 반환
        return new DefaultOidcUser(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().name())),
                oidcUser.getIdToken(), // ID Token 포함
                oidcUser.getUserInfo(), // UserInfo Endpoint 정보 포함 (선택적)
                userNameAttributeName); // ID Token에서 사용자 이름으로 사용할 속성 키
    }

    // CustomOAuth2UserService와 동일한 로직 사용 가능 (중복 제거 고려)
    private User saveOrUpdate(OAuthAttributes attributes) {
        Optional<User> userOptional = userRepository.findByProviderAndProviderId(attributes.provider(), attributes.providerId());

        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            user.updateOAuthInfo(attributes.name());
            log.info("Existing user found via OIDC: provider={}, providerId={}, email={}",
                    attributes.provider(), attributes.providerId(), user.getEmail());
        } else {
            Optional<User> existingEmailUser = userRepository.findByEmail(attributes.email());
            if (existingEmailUser.isPresent()) {
                log.warn("Email already exists: {}", attributes.email());
                throw new OAuth2Exception.EmailAlreadyExistsException(attributes.email());
            }
            user = attributes.toEntity();
            userRepository.save(user);
            log.info("New user registered via OIDC: provider={}, providerId={}, email={}",
                    attributes.provider(), attributes.providerId(), user.getEmail());
        }
        return user;
    }
}
