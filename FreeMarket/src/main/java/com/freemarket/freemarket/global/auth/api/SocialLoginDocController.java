package com.freemarket.freemarket.global.auth.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "소셜 로그인 (OAuth2)", description = "소셜 로그인 시작 (문서용)")
@RestController
public class SocialLoginDocController {

    @Operation(summary = "소셜 로그인 시작 (Google, Kakao, Naver 등)",
            description = "이 경로로 GET 요청을 보내면(브라우저에서 링크 클릭) 해당 소셜 서비스 로그인 페이지로 리다이렉션됩니다. API 클라이언트에서 직접 호출하는 용도가 아닙니다." +
                    "http://localhost:8081/oauth/callback로 콜백 요청을 보냅니다.")
    @GetMapping("/oauth2/authorization/{provider}")
    public void socialLoginRedirect(@PathVariable String provider) {
        // 실제 로직은 없음. Spring Security가 처리.
        // 이 메서드는 Swagger 문서 생성을 위해 존재.
    }
}
