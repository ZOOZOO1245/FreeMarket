package com.freemarket.freemarket.user.api;

import com.freemarket.freemarket.global.common.ResponseDTO;
import com.freemarket.freemarket.global.security.CustomUserDetails;
import com.freemarket.freemarket.user.api.dto.UserProfileDto;
import com.freemarket.freemarket.user.application.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/users/profile")
@RequiredArgsConstructor
@Tag(name = "사용자 프로필", description = "사용자 프로필 관련 API")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @Operation(summary = "프로필 요약 정보 조회", description = "로그인한 사용자의 프로필 요약 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "프로필 요약 정보 조회 성공")
    @GetMapping("/summary")
    public ResponseEntity<ResponseDTO<UserProfileDto.ProfileSummaryResponse>> getProfileSummary(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {

        log.info("프로필 요약 정보 조회: 사용자 ID {}", userDetails.getUserId());

        UserProfileDto.ProfileSummaryResponse response = userProfileService.getProfileSummary(userDetails.getUserId());

        return ResponseEntity.ok(ResponseDTO.success(response));
    }

    @Operation(summary = "판매 내역 조회", description = "로그인한 사용자의 판매 내역을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "판매 내역 조회 성공")
    @GetMapping("/selling")
    public ResponseEntity<ResponseDTO<UserProfileDto.SellingHistoryResponse>> getSellingHistory(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "페이지네이션 정보")
            @PageableDefault(size = 10) Pageable pageable) {

        log.info("판매 내역 조회: 사용자 ID {}", userDetails.getUserId());
        UserProfileDto.SellingHistoryResponse response = userProfileService.getSellingHistory(userDetails.getUserId(), pageable);

        return ResponseEntity.ok(ResponseDTO.success(response));
    }

    @Operation(summary = "구매 내역 조회", description = "로그인한 사용자의 구매 내역을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "구매 내역 조회 성공")
    @GetMapping("/me/purchases")
    public ResponseEntity<ResponseDTO<UserProfileDto.PurchaseHistoryResponse>> getPurchaseHistory(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "페이지네이션 정보")
            @PageableDefault(size = 10) Pageable pageable) {

        log.info("구매 내역 조회: 사용자 ID {}", userDetails.getUserId());
        UserProfileDto.PurchaseHistoryResponse response =
                userProfileService.getPurchaseHistory(userDetails.getUserId(), pageable);

        return ResponseEntity.ok(ResponseDTO.success(response));
    }

    @Operation(summary = "다른 사용자 프로필 조회", description = "다른 사용자의 프로필 정보를 조회합니다. (제한된 정보만 제공)")
    @ApiResponse(responseCode = "200", description = "사용자 프로필 조회 성공")
    @GetMapping("/{userId}")
    public ResponseEntity<ResponseDTO<UserProfileDto.ProfileSummaryResponse>> getUserProfile(
            @Parameter(description = "조회할 사용자 ID", required = true) @PathVariable Long userId) {

        log.info("사용자 프로필 조회: 사용자 ID {}", userId);
        UserProfileDto.ProfileSummaryResponse response = userProfileService.getProfileSummary(userId);

        return ResponseEntity.ok(ResponseDTO.success(response));
    }
}
