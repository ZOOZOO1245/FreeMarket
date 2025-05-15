package com.freemarket.freemarket.product.api;

import com.freemarket.freemarket.global.common.ResponseDTO;
import com.freemarket.freemarket.global.security.CustomUserDetails;
import com.freemarket.freemarket.product.api.dto.ProductDto;
import com.freemarket.freemarket.product.application.ProductWishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "상품 관심 등록 API", description = "상품 관심 등록(찜하기) 관련 API")
public class ProductWishlistController {

    private final ProductWishlistService wishlistService;

    @Operation(summary = "상품 관심 등록/취소", description = "상품을 관심 목록에 추가하거나 제거합니다. 이미 관심 등록된 상품은 취소됩니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "성공적으로 처리됨",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음")
    })
    @PostMapping("/{productId}/wishlist")
    public ResponseEntity<Map<String, Object>> toggleWishlist(
            @Parameter(description = "관심 등록/취소할 상품 ID", required = true)
            @PathVariable Long productId,

            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {

        boolean isAdded = wishlistService.toggleWishlist(userDetails.getUserId(), productId);
        Long wishlistCount = wishlistService.getWishlistCount(productId);

        return ResponseEntity.ok(Map.of(
                "added", isAdded,
                "count", wishlistCount
        ));
    }

    @Operation(summary = "사용자의 관심 상품 목록 조회", description = "현재 로그인한 사용자가 관심 등록한 상품 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "성공적으로 조회됨",
                    content = @Content(schema = @Schema(implementation = List.class))
            ),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @GetMapping("/wishlist")
    public ResponseEntity<ResponseDTO<List<ProductDto.ProductBaseResponse>>> getUserWishlist(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<ProductDto.ProductBaseResponse> wishlist = wishlistService.getUserWishlistDto(userDetails.getUserId());
        return ResponseEntity.ok(ResponseDTO.success(wishlist));
    }
}
