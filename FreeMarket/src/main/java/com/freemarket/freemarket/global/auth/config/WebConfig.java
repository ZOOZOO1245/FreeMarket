package com.freemarket.freemarket.global.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}") private String uploadPath;
    @Value("${file.thumbnail-dir}") private String thumbnailPath;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // /api/** 경로에 대해 CORS 적용
                .allowedOrigins("http://localhost:8081", "http://127.0.0.1:8081") // 허용할 출처 (Vue 개발 서버 주소)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // 허용할 HTTP 메서드
                .allowedHeaders("*") // 모든 헤더 허용
                .allowCredentials(true) // 인증 정보(쿠키, JWT 등) 허용
                .maxAge(3600); // pre-flight 요청 캐시 시간 (초)
        // 필요시 다른 경로 추가 (예: /oauth2/**)
        registry.addMapping("/oauth2/**")
                .allowedOrigins("http://localhost:8081", "http://127.0.0.1:8081")
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
        registry.addMapping("/login/oauth2/code/**")
                .allowedOrigins("http://localhost:8081", "http://127.0.0.1:8081")
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + uploadPath + "/"); // 경로 끝 '/' 중요
        registry.addResourceHandler("/thumbnails/**")
                .addResourceLocations("file:" + thumbnailPath + "/"); // 경로 끝 '/' 중요
    }
}
