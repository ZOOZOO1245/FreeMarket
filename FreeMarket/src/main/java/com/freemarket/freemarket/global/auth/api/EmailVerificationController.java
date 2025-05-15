package com.freemarket.freemarket.global.auth.api;

import com.freemarket.freemarket.global.auth.api.dto.EmailVerificationDto;
import com.freemarket.freemarket.global.auth.application.EmailVerificationService;
import com.freemarket.freemarket.global.common.ResponseDTO;
import com.freemarket.freemarket.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/email-verification")
@RequiredArgsConstructor
@Tag(name = "Email Verification", description = "이메일 인증 API")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @PostMapping("/send")
    @Operation(summary = "인증 이메일 발송", description = "학교 이메일로 인증 코드를 발송합니다.")
    public ResponseEntity<ResponseDTO<EmailVerificationDto.EmailVerificationResponse>> sendVerificationEmail(
            @Parameter(description = "이메일")
            @Valid @RequestBody EmailVerificationDto.EmailVerificationRequest request) throws BadRequestException {

        EmailVerificationDto.EmailVerificationResponse response = emailVerificationService.sendVerificationEmail(request);
        return ResponseEntity.ok(ResponseDTO.success(response));
    }

    @PostMapping("/verify")
    @Operation(summary = "인증 코드 확인", description = "발송된 인증 코드를 확인합니다.")
    public ResponseEntity<ResponseDTO<EmailVerificationDto.EmailVerificationResponse>> verifyEmail(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "이메일과 인증 코드 정보")
            @Valid @RequestBody EmailVerificationDto.VerificationCodeRequest request) throws BadRequestException {

        EmailVerificationDto.EmailVerificationResponse response = emailVerificationService.verifyEmail(userDetails.getEmail(), request);
        return ResponseEntity.ok(ResponseDTO.success(response));
    }
}
