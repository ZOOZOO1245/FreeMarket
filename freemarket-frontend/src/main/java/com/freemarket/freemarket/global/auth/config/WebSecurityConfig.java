package com.freemarket.freemarket.global.auth.config;

import com.freemarket.freemarket.global.jwt.JwtFilter;
import com.freemarket.freemarket.global.jwt.JwtProvider;
import com.freemarket.freemarket.global.oauth2.application.CustomOAuth2UserService;
import com.freemarket.freemarket.global.oauth2.application.CustomOidcUserService;
import com.freemarket.freemarket.global.oauth2.handler.OAuth2LoginFailureHandler;
import com.freemarket.freemarket.global.oauth2.handler.OAuth2LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtProvider jwtProvider;
    // OAuth2 관련 빈 주입
    private final CustomOAuth2UserService customOAuth2UserService; // 네이버용
    private final CustomOidcUserService customOidcUserService;   // 카카오, 구글용 (OIDC)
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtProvider jwtProvider) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // REST API이므로 CSRF 보안 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                // 세션 관리: STATELESS (JWT 사용)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 요청별 인가 설정
                .authorizeHttpRequests(authorize -> authorize
                        // 공개 경로 설정
                        // 정적 리소스, 기본 페이지, 인증/OAuth 관련 API 등 명시적 허용
                        .requestMatchers("/", "/index.html", "/favicon.ico", "/static/**", "/assets/**", "/js/**", "/css/**" ).permitAll()
                        // // --- Vue Router가 처리할 경로들 ---
                        .requestMatchers("/reset-password",  "/oauth/callback").permitAll()

                        .requestMatchers("/api/auth/**").permitAll() // 일반 로그인/회원가입 API
                        .requestMatchers("/oauth2/**").permitAll()            // OAuth2 로그인 시작 URL (e.g., /oauth2/authorization/google)
                        .requestMatchers("/login/oauth2/code/**").permitAll() // OAuth2 리다이렉션 URI
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // Swagger 허용
                        .requestMatchers("/h2-console/**").permitAll() // H2 콘솔 허용

                        // /api 로 시작하는 경로는 인증 요구 (위에서 permitAll된 /api/auth/** 제외)
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll() // 나머지 요청은 허용
                )
                .oauth2Login(oauth2 -> oauth2
                        // 사용자 정보 엔드포인트 설정
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService) // OAuth2 (naver)
                                .oidcUserService(customOidcUserService) // OIDC (google, kakao)
                        )

                        // 로그인 성공 / 실패 핸들러
                        .successHandler(oAuth2LoginSuccessHandler) // 성공 시 JWT 발급 및 JSON 응답
                        .failureHandler(oAuth2LoginFailureHandler) // 실패 시 JSON 에러 응답

                        // .authorizationEndpoint(endpoint -> endpoint // 로그인 페이지 경로 커스텀 시
                        //         .baseUri("/oauth2/authorization") // 기본값: /oauth2/authorization/{registrationId}
                        // )
                        // .redirectionEndpoint(endpoint -> endpoint // 리다이렉션 URI 경로 커스텀 시
                        //         .baseUri("/login/oauth2/code/*")   // 기본값: /login/oauth2/code/{registrationId}
                        // )
                )
                .headers(headers -> headers.frameOptions(options -> options.sameOrigin())) // H2 콘솔 사용을 위한 설정
                .addFilterBefore(new JwtFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
