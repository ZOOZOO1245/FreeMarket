package com.freemarket.freemarket.product.application;

import com.freemarket.freemarket.global.file.application.FileStorageService;
import com.freemarket.freemarket.product.api.dto.ProductDto;
import com.freemarket.freemarket.product.domain.*;
import com.freemarket.freemarket.user.domain.User;
import com.freemarket.freemarket.user.domain.UserRepository;
import com.freemarket.freemarket.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    // 상품 등록
    @Transactional
    public ProductDto.ProductResponse createProduct(Long userId, ProductDto.CreateRequest request, List<MultipartFile> images) {
        User seller = userRepository.findById(userId)
                .orElseThrow(() -> new UserException.UserNotFoundException(userId));

        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .stock(request.stock())
                .category(request.category())
                .seller(seller)
                .build();

        List<FileStorageService.FileUploadResult> uploadedFiles = new ArrayList<>();
        try {
            if (images != null && !images.isEmpty()) {
                for (int i = 0; i < images.size(); i++) {
                    MultipartFile imageFile = images.get(i);
                    FileStorageService.FileUploadResult result = fileStorageService.storeFile(imageFile);
                    uploadedFiles.add(result);
                    boolean isThumbnail = (i == 0); // 첫 번째 이미지 -> 썸네일
                    ProductImage productImage = ProductImage.builder()
                            .imageUrl(result.originalFilePath())
                            .thumbnailUrl(result.thumbnailFilePath())
                            .originalFileName(result.originalFileName())
                            .displayOrder(i)
                            .isThumbnail(isThumbnail)
                            .build();
                    product.addImage(productImage); // Product 와 ProductImage 연결
                }
            }
            Product savedProduct = productRepository.save(product);
            log.info("상품 등록 완료: 상품명 {}, 판매자 ID {}", request.name(), userId);
            return ProductDto.ProductResponse.from(savedProduct);
        } catch (Exception e) {
            log.error("상품 등록 중 오류 발생: 사용자 ID {}", userId, e);
            uploadedFiles.forEach(uploadedFile -> {
                fileStorageService.deleteFile(uploadedFile.originalFilePath());
                fileStorageService.deleteFile(uploadedFile.thumbnailFilePath());
            });
            throw new RuntimeException("상품 등록 중 오류가 발생했습니다.", e);
        }
    }

    // 상품 수정
    @Transactional
    public ProductDto.ProductResponse updateProduct(Long userId, Long productId, ProductDto.UpdateRequest request,
                                                    List<MultipartFile> newImages, List<Long> deleteImageIds) {
        Product product = getProductWithSellerCheck(productId, userId);
        product.update(request.name(), request.description(), request.price(), request.stock(), request.category());
        if (request.status() != null) product.changeStatus(request.status());

        boolean thumbnailDeleted = false; // 썸네일 삭제 여부 추적
        if (deleteImageIds != null && !deleteImageIds.isEmpty()) {
            List<ProductImage> imagesToDelete = product.getImages().stream().filter(img -> deleteImageIds.contains(img.getId())).toList();
            thumbnailDeleted = imagesToDelete.stream().anyMatch(ProductImage::isThumbnail);
            imagesToDelete.forEach(image -> {
                fileStorageService.deleteFile(image.getImageUrl());
                fileStorageService.deleteFile(image.getThumbnailUrl());
                product.removeImage(image);
                log.info("상품 ID {}의 이미지 삭제: {}", productId, image.getOriginalFileName());
            });
        }

        List<FileStorageService.FileUploadResult> newlyUploadedFiles = new ArrayList<>();
        try {
            if (newImages != null && !newImages.isEmpty()) {
                int currentImageCount = product.getImages().size();
                for (int i = 0; i < newImages.size(); i++) {
                    MultipartFile imageFile = newImages.get(i);
                    if (imageFile == null || imageFile.isEmpty()) continue;
                    FileStorageService.FileUploadResult result = fileStorageService.storeFile(imageFile);
                    newlyUploadedFiles.add(result);
                    // 썸네일 지정: (현재 이미지가 없고 추가하는 첫 이미지) OR (기존 썸네일 삭제되었고 추가하는 첫 이미지)
                    boolean isThumbnail = (product.getImages().isEmpty() && i == 0) || (thumbnailDeleted && i == 0);
                    ProductImage productImage = ProductImage.builder()
                            .imageUrl(result.originalFilePath()).thumbnailUrl(result.thumbnailFilePath())
                            .originalFileName(result.originalFileName()).displayOrder(currentImageCount + i).isThumbnail(isThumbnail)
                            .build();
                    product.addImage(productImage);
                    log.info("상품 ID {}에 새 이미지 추가: {}", productId, imageFile.getOriginalFilename());
                }
            }

            // 썸네일 재조정: 이미지가 있는데 썸네일이 없는 경우 -> 첫 번째 이미지 썸네일로
            if (!product.getImages().isEmpty() && product.getImages().stream().noneMatch(ProductImage::isThumbnail)) {
                product.getImages().forEach(img -> img.setThumbnail(false)); // 모두 false 초기화
                product.getImages().get(0).setThumbnail(true); // 첫 번째를 썸네일로
                log.info("상품 ID {}의 썸네일 재지정 완료 (업데이트 중)", productId);
            }

            log.info("상품 수정 완료: 상품 ID {}, 판매자 ID {}", productId, userId);
            return ProductDto.ProductResponse.from(product); // 변경 감지로 업데이트됨
        } catch (Exception e) { // 롤백 로직
            log.error("상품 수정 중 오류 발생: 상품 ID {}", productId, e);
            newlyUploadedFiles.forEach(uploadedFile -> {
                fileStorageService.deleteFile(uploadedFile.originalFilePath());
                fileStorageService.deleteFile(uploadedFile.thumbnailFilePath());
            });
            throw new RuntimeException("상품 수정 중 오류가 발생했습니다.", e);
        }
    }

    // 상품 단건 조회
    public ProductDto.ProductResponse getProduct(Long productId) {
        Product product = findProduct(productId);
        return ProductDto.ProductResponse.from(product);
    }

    // 상품 목록 조회
    public Page<ProductDto.ProductResponse> getProducts(String keyword, ProductStatus status, Pageable pageable) {
        return productRepository.findAllWithFilters(keyword, status, pageable)
                .map(ProductDto.ProductResponse::from);
    }

    // 카테고리별 상품 조회
    public Page<ProductDto.ProductResponse> getProductsByCategory(ProductCategory category, String keyword, ProductStatus status,
                                                                  Pageable pageable) {
        return productRepository.findByCategoryWithFilters(category, keyword, status, pageable)
                .map(ProductDto.ProductResponse::from);
    }

    // 상품 삭제
    @Transactional
    public void deleteProduct(Long userId, Long productId) {
        Product product = getProductWithSellerCheck(productId, userId);
        productRepository.delete(product);
        log.info("상품 삭제 완료: 상품 ID {}, 판매자 ID {}", productId, userId);
    }

    // 판매자 권한 확인 후 상품 조회
    private Product getProductWithSellerCheck(Long productId, Long userId) {
        Product product = findProduct(productId);

        if (!product.getSeller().getId().equals(userId)) {
            throw new RuntimeException("해당 상품의 판매자가 아닙니다.");
        }

        return product;
    }

    private Product findProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다: " + productId));
        return product;
    }

}
