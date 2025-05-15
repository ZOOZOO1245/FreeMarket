package com.freemarket.freemarket.global.auth.api;

import com.freemarket.freemarket.global.auth.api.dto.AuthDto;
import com.freemarket.freemarket.global.auth.application.AuthService;
import com.freemarket.freemarket.global.common.ResponseDTO;
import com.freemarket.freemarket.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증", description = "인증 관련 API")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "이메일, 비밀번호, 이름, 전화번호를 입력받아 회원가입을 진행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "409", description = "이미 사용 중인 이메일")
    })
    @PostMapping("/signup")
    public ResponseEntity<ResponseDTO<Void>> signup(
            @Parameter(description = "회원가입 정보", required = true)
            @Valid @RequestBody AuthDto.SignupRequest request) {
        log.info("회원가입 컨트롤러 호출: {}", request.email());
        authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDTO.success(null, "회원가입이 완료되었습니다."));
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호를 입력받아 로그인하고 JWT 토큰을 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<AuthDto.TokenResponse>> login(
            @Parameter(description = "로그인 정보", required = true)
            @Valid @RequestBody AuthDto.LoginRequest request) {
        log.info("로그인 컨트롤러 호출: {}", request.email());
        AuthDto.TokenResponse tokenResponse = authService.login(request);
        return ResponseEntity.ok(ResponseDTO.success(tokenResponse, "로그인에 성공했습니다."));
    }

    @Operation(summary = "토큰 갱신", description = "리프레시 토큰을 사용하여 액세스 토큰을 갱신합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 갱신 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰")
    })
    @PostMapping("/refresh")
    public ResponseEntity<ResponseDTO<AuthDto.TokenResponse>> refreshToken(
            @Parameter(description = "리프레시 토큰", required = true)
            @Valid @RequestBody AuthDto.TokenRefreshRequest request) {
        log.info("토큰 갱신 컨트롤러 호출");
        AuthDto.TokenResponse tokenResponse = authService.refreshToken(request);
        return ResponseEntity.ok(ResponseDTO.success(tokenResponse, "토큰이 갱신되었습니다."));
    }

    @Operation(summary = "로그아웃", description = "사용자의 인증 정보를 제거하고 리프레시 토큰을 무효화합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @PostMapping("/logout")
    public ResponseEntity<ResponseDTO<Void>> logout() {
        log.info("로그아웃 컨트롤러 호출");
        // SecurityContext에서 현재 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            authService.logout(userDetails.getUserId());
        }
        return ResponseEntity.ok(ResponseDTO.success(null, "로그아웃 되었습니다."));
    }
}
