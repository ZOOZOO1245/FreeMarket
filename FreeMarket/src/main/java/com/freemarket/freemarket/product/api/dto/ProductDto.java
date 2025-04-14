package com.freemarket.freemarket.product.api.dto;

import com.freemarket.freemarket.product.domain.Product;
import com.freemarket.freemarket.product.domain.ProductCategory;
import com.freemarket.freemarket.product.domain.ProductImage;
import com.freemarket.freemarket.product.domain.ProductStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

public class ProductDto {

    public record CreateRequest(
            @NotBlank(message = "상품명은 필수 입력값입니다.")
            String name,

            String description,

            @NotNull(message = "가격은 필수 입력값입니다.")
            @Min(value = 0)
            Long price,

            @NotNull(message = "재고는 필수 입력값입니다.")
            @Min(value = 1, message = "재고는 최소 1개 이상이어야 합니다.")
            Integer stock,

            @NotNull(message = "카테고리는 필수 입력값입니다.")
            ProductCategory category
    ) {}

    public record UpdateRequest(
            @NotBlank(message = "상품명은 필수 입력값입니다.")
            String name,

            String description,

            @NotNull(message = "가격은 필수 입력값입니다.")
            @Min(value = 100, message = "가격은 최소 100원 이상이어야 합니다.")
            Long price,

            @NotNull(message = "재고는 필수 입력값입니다.")
            @Min(value = 1, message = "재고는 최소 1개 이상이어야 합니다.")
            Integer stock,

            @NotNull(message = "카테고리는 필수 입력값입니다.")
            ProductCategory category,

            ProductStatus status
    ){}

    @Builder
    public record ProductResponse(
            Long id,
            String name,
            String description,
            Long price,
            Integer stock,
            String category,
            String status,
            String thumbnailUrl,
            List<String> imageUrls,
            Long sellerId,
            String sellerName
    ) {
        public static ProductResponse from(Product product) {
            return ProductResponse.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .description(product.getDescription())
                    .price(product.getPrice())
                    .stock(product.getStock())
                    .category(product.getCategory().getDisplayName())
                    .status(product.getStatus().getDisplayName())
                    .thumbnailUrl(product.getRepresentativeThumbnailUrl())
                    .imageUrls(product.getImages().stream()
                            .map(ProductImage::getImageUrl)
                            .collect(Collectors.toList()))
                    .sellerId(product.getSeller().getId())
                    .sellerName(product.getSeller().getName())
                    .build();
        }
    }
}
