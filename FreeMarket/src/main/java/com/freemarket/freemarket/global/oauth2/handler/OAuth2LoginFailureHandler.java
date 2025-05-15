package com.freemarket.freemarket.global.oauth2.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freemarket.freemarket.global.common.ResponseDTO;
import com.freemarket.freemarket.global.exception.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.error("OAuth2 Authentication Failed: {}", exception.getMessage(), exception);

        HttpStatus status = HttpStatus.UNAUTHORIZED; // 기본값: 인증 실패
        String message = "소셜 로그인에 실패했습니다. 다시 시도해주세요.";
        String errorCode = "OAUTH2_AUTH_FAILED";

        // 특정 예외에 따라 메시지나 상태 코드 변경 가능
        // 예: 이메일 중복 예외 (UserService에서 발생시킨 경우)
        // if (exception.getCause() instanceof YourCustomEmailDuplicateException) {
        //     status = HttpStatus.CONFLICT;
        //     message = exception.getMessage(); // 예외 메시지 사용
        //     errorCode = "EMAIL_DUPLICATE";
        // }

        ErrorResponse errorData = ErrorResponse.builder()
                .status(status.value())
                .message(message)
                .errorCode(errorCode)
                .timestamp(LocalDateTime.now())
                .errors(List.of()) // OAuth 실패는 특정 필드 에러가 아님
                .build();

        ResponseDTO<ErrorResponse> responseDTO = ResponseDTO.error(status.value(), message, errorData);

        // JSON 응답 전송
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(responseDTO));
        response.getWriter().flush();
    }
}
