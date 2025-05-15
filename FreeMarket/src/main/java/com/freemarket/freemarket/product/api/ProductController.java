package com.freemarket.freemarket.product.api;

import com.freemarket.freemarket.global.common.ResponseDTO;
import com.freemarket.freemarket.global.security.CustomUserDetails;
import com.freemarket.freemarket.product.api.dto.ProductDto;
import com.freemarket.freemarket.product.application.ProductManagementService;
import com.freemarket.freemarket.product.application.ProductQueryService;
import com.freemarket.freemarket.product.application.ProductStatusService;
import com.freemarket.freemarket.product.application.ProductViewService;
import com.freemarket.freemarket.product.domain.ProductCategory;
import com.freemarket.freemarket.product.domain.ProductStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "상품", description = "상품 관련 API")
public class ProductController {

    private final ProductManagementService productManagementService;
    private final ProductStatusService productStatusService;
    private final ProductQueryService productQueryService;
    private final ProductViewService viewService;

    @Operation(summary = "상품 등록", description = "새로운 상품을 등록합니다. 상품명, 가격, 수량, 카테고리는 필수 입력 항목입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "상품 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(schema = @Schema(implementation = ResponseDTO.class)))
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO<ProductDto.ProductBaseResponse>> createProduct(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "상품 등록 정보", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            @Valid @RequestPart("request") ProductDto.CreateRequest request,
            @Parameter(description = "이미지 파일") @RequestPart(value = "images", required = false)List<MultipartFile> images) throws BadRequestException {

        log.info("상품 등록 요청: 사용자 ID {}", userDetails.getUserId());
        ProductDto.ProductBaseResponse response = productManagementService.createProduct(userDetails.getUserId(), request, images);

        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDTO.success(response, "상품이 성공적으로 등록되었습니다."));
    }

    @Operation(summary = "상품 수정", description = "상품 ID로 기존 상품 정보를 수정합니다. 본인이 등록한 상품만 수정 가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음")
    })
    @PatchMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO<ProductDto.ProductBaseResponse>> updateProduct(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "상품 ID", required = true) @PathVariable Long productId,
            @Parameter(description = "상품 정보 (JSON)", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            @Valid @RequestPart("request") ProductDto.UpdateRequest request,
            @Parameter(description = "추가/교체할 이미지") @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages,
            @Parameter(description = "삭제할 이미지 ID") @RequestParam(value = "deleteImageIds", required = false) List<Long> deleteImageIds
    ) {

        log.info("상품 수정 요청: 상품 ID {}, 사용자 ID {}", productId, userDetails.getUserId());
        ProductDto.ProductBaseResponse response = productManagementService.updateProduct(userDetails.getUserId(), productId, request, newImages, deleteImageIds);

        return ResponseEntity.ok(ResponseDTO.success(response, "상품이 성공적으로 수정되었습니다."));
    }

    @Operation(summary = "상품 조회", description = "상품 ID로 상품 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 조회 성공"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음")
    })
    @GetMapping("/{productId}")
    public ResponseEntity<ResponseDTO<ProductDto.ProductDetailResponse>> getProduct(
            @Parameter(description = "상품 ID", required = true) @PathVariable Long productId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {

        // 조회수 증가
        viewService.incrementViewCount(productId);
        log.info("상품 조회 요청: 상품 ID {}", productId);
        ProductDto.ProductDetailResponse response = productQueryService.getProduct(productId, userDetails.getUserId());

        return ResponseEntity.ok(ResponseDTO.success(response));
    }

    @Operation(summary = "상품 목록 조회", description = "상품 목록을 필터링하여 조회합니다. 키워드 검색과 상태 필터링이 가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 목록 조회 성공")
    })
    @GetMapping
    public ResponseEntity<ResponseDTO<Page<ProductDto.ProductDetailResponse>>> getProducts(
            @Parameter(description = "검색 키워드 (상품명, 설명에서 검색)")
            @RequestParam(required = false) String keyword,

            @Parameter(description = "상품 상태 (ACTIVE: 판매중, SOLD_OUT: 품절, DISCONTINUED: 판매중단, PENDING: 승인대기)")
            @RequestParam(required = false) ProductStatus status,

            @Parameter(description = "페이지네이션 정보 (기본값: 페이지 크기 10, 생성일 기준 내림차순 정렬)")
            @PageableDefault(size = 10, sort = "createdDate") Pageable pageable,

            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {

        log.info("상품 목록 조회 요청: 키워드 {}, 상태 {}", keyword, status);
        Page<ProductDto.ProductDetailResponse> response = productQueryService.getProducts(keyword, status, pageable, userDetails.getUserId());

        return ResponseEntity.ok(ResponseDTO.success(response));
    }

    @Operation(summary = "카테고리별 상품 조회", description = "특정 카테고리에 해당하는 상품 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리별 상품 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 카테고리 요청")
    })
    @GetMapping("/category/{category}")
    public ResponseEntity<ResponseDTO<Page<ProductDto.ProductDetailResponse>>> getProductsByCategory(
            @Parameter(description = "상품 카테고리 (BOOKS, ELECTRONICS, FASHION 등)", required = true)
            @PathVariable ProductCategory category,

            @Parameter(description = "검색 키워드 (상품명, 설명에서 검색)")
            @RequestParam(required = false) String keyword,

            @Parameter(description = "상품 상태 (ACTIVE: 판매중, SOLD_OUT: 품절 등)")
            @RequestParam(required = false) ProductStatus status,

            @Parameter(description = "페이지네이션 정보")
            @PageableDefault(size = 10, sort = "createdDate") Pageable pageable,

            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {

        log.info("카테고리별 상품 조회 요청: 카테고리 {}, 키워드 {}, 상태 {}", category, keyword, status);
        Page<ProductDto.ProductDetailResponse> response = productQueryService.getProductsByCategory(category, keyword, status, pageable, userDetails.getUserId());

        return ResponseEntity.ok(ResponseDTO.success(response));
    }

    @Operation(summary = "상품 삭제", description = "상품 ID로 상품을 삭제합니다. 본인이 등록한 상품만 삭제 가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음")
    })
    @DeleteMapping("/{productId}")
    public ResponseEntity<ResponseDTO<Void>> deleteProduct(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "삭제할 상품 ID", required = true) @PathVariable Long productId) {

        log.info("상품 삭제 요청: 상품 ID {}, 사용자 ID {}", productId, userDetails.getUserId());
        productManagementService.deleteProduct(userDetails.getUserId(), productId);

        return ResponseEntity.ok(ResponseDTO.success(null, "상품이 성공적으로 삭제되었습니다."));
    }

    @Operation(summary = "상품 판매완료 처리", description = "상품을 판매완료 상태로 변경하고 구매자를 지정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "판매완료 처리 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음")
    })
    @PostMapping("/{productId}/sold")
    public ResponseEntity<ResponseDTO<ProductDto.ProductBaseResponse>> markProductAsSold(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "상품 ID", required = true) @PathVariable Long productId,
            @Parameter(description = "구매자 ID", required = true) @RequestParam Long buyerId) {

        log.info("상품 판매완료 처리 요청: 상품 ID {}, 판매자 ID {}, 구매자 ID {}",
                productId, userDetails.getUserId(), buyerId);

        ProductDto.ProductBaseResponse response = productStatusService.markProductAsSold(
                userDetails.getUserId(), productId, buyerId);

        return ResponseEntity.ok(ResponseDTO.success(response, "상품이 판매완료 처리되었습니다."));
    }

    @Operation(summary = "판매완료 취소", description = "판매완료 상태의 상품을 다시 판매중 상태로 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "판매완료 취소 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음")
    })
    @PostMapping("/{productId}/cancel-sold")
    public ResponseEntity<ResponseDTO<ProductDto.ProductBaseResponse>> cancelProductSold(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "상품 ID", required = true) @PathVariable Long productId) {

        log.info("판매완료 취소 요청: 상품 ID {}, 판매자 ID {}", productId, userDetails.getUserId());

        ProductDto.ProductBaseResponse response = productStatusService.cancelProductSold(
                userDetails.getUserId(), productId);

        return ResponseEntity.ok(ResponseDTO.success(response, "판매완료가 취소되었습니다."));
    }
}
