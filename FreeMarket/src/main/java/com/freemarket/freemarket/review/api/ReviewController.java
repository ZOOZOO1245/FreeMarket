package com.freemarket.freemarket.review.api;

import com.freemarket.freemarket.global.common.ResponseDTO;
import com.freemarket.freemarket.global.security.CustomUserDetails;
import com.freemarket.freemarket.review.api.dto.ReviewDto;
import com.freemarket.freemarket.review.application.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "리뷰", description = "리뷰 관련 API")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 작성", description = "구매한 상품에 대해 판매자에게 리뷰를 작성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "리뷰 작성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "상품 또는 사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "이미 리뷰가 존재함")
    })
    @PostMapping
    public ResponseEntity<ResponseDTO<ReviewDto.ReviewResponse>> createReview(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "리뷰 작성 정보", required = true)
            @Valid @RequestBody ReviewDto.ReviewCreateRequest request) {

        log.info("리뷰 작성 요청: 사용자 ID {}, 상품 ID {}", userDetails.getUserId(), request.productId());
        ReviewDto.ReviewResponse response = reviewService.createReview(userDetails.getUserId(), request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDTO.success(response, "리뷰가 성공적으로 작성되었습니다."));
    }

    @Operation(summary = "리뷰 수정", description = "자신이 작성한 리뷰를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음")
    })
    @PatchMapping("/{reviewId}")
    public ResponseEntity<ResponseDTO<ReviewDto.ReviewResponse>> updateReview(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "수정할 리뷰 ID", required = true) @PathVariable Long reviewId,
            @Parameter(description = "리뷰 수정 정보", required = true)
            @Valid @RequestBody ReviewDto.ReviewUpdateRequest request) {

        log.info("리뷰 수정 요청: 사용자 ID {}, 리뷰 ID {}", userDetails.getUserId(), reviewId);
        ReviewDto.ReviewResponse response = reviewService.updateReview(userDetails.getUserId(), reviewId, request);

        return ResponseEntity.ok(ResponseDTO.success(response, "리뷰가 성공적으로 수정되었습니다."));
    }

    @Operation(summary = "리뷰 삭제", description = "자신이 작성한 리뷰를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음")
    })
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ResponseDTO<Void>> deleteReview(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "삭제할 리뷰 ID", required = true) @PathVariable Long reviewId) {

        log.info("리뷰 삭제 요청: 사용자 ID {}, 리뷰 ID {}", userDetails.getUserId(), reviewId);
        reviewService.deleteReview(userDetails.getUserId(), reviewId);

        return ResponseEntity.ok(ResponseDTO.success(null, "리뷰가 성공적으로 삭제되었습니다."));
    }

    @Operation(summary = "리뷰 상세 조회", description = "특정 리뷰의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 조회 성공"),
            @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음")
    })
    @GetMapping("/{reviewId}")
    public ResponseEntity<ResponseDTO<ReviewDto.ReviewResponse>> findReview(
            @Parameter(description = "조회할 리뷰 ID", required = true) @PathVariable Long reviewId) {

        log.info("리뷰 상세 조회 요청: 리뷰 ID {}", reviewId);
        ReviewDto.ReviewResponse response = reviewService.findReview(reviewId);

        return ResponseEntity.ok(ResponseDTO.success(response));
    }

    @Operation(summary = "사용자가 받은 리뷰 목록 조회", description = "특정 사용자가 판매자로서 받은 리뷰 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/users/{userId}")
    public ResponseEntity<ResponseDTO<ReviewDto.ReviewListResponse>> getUserReviews(
            @Parameter(description = "조회할 사용자 ID (판매자)", required = true) @PathVariable Long userId,
            @Parameter(description = "페이지네이션 정보") @PageableDefault(size = 10) Pageable pageable) {

        log.info("사용자가 받은 리뷰 목록 조회 요청: 사용자 ID {}", userId);
        ReviewDto.ReviewListResponse response = reviewService.getUserReviews(userId, pageable);

        return ResponseEntity.ok(ResponseDTO.success(response));
    }

    @Operation(summary = "내가 작성한 리뷰 목록 조회", description = "로그인한 사용자가 작성한 리뷰 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/me")
    public ResponseEntity<ResponseDTO<ReviewDto.MyReviewListResponse>> getMyReviews(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "페이지네이션 정보") @PageableDefault(size = 10) Pageable pageable) {

        log.info("내가 작성한 리뷰 목록 조회 요청: 사용자 ID {}", userDetails.getUserId());
        ReviewDto.MyReviewListResponse response = reviewService.getMyReviews(userDetails.getUserId(), pageable);

        return ResponseEntity.ok(ResponseDTO.success(response));
    }
}
