package com.freemarket.freemarket.product.domain;

import com.freemarket.freemarket.product.api.dto.ProductWithStatsDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.freemarket.freemarket.product.domain.QProduct.product;
import static com.freemarket.freemarket.product.domain.QProductImage.productImage;
import static com.freemarket.freemarket.product.domain.QProductViewCount.productViewCount;
import static com.freemarket.freemarket.product.domain.QProductWishlist.productWishlist;

@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Product> findAllWithFilters(String keyword, ProductStatus status, Pageable pageable) {
        // 공통 조건을 사용하여 BooleanBuilder 생성
        BooleanBuilder builder = createBasicCondition(keyword, status, null);

        // 페이지 조회 및 생성 공통 메서드 호출
        return fetchPagedProductsWithFetchJoin(builder, pageable);
    }

    @Override
    public Page<Product> findByCategoryWithFilters(ProductCategory category, String keyword, ProductStatus status, Pageable pageable) {
        // 공통 조건을 사용하여 BooleanBuilder 생성
        BooleanBuilder builder = createBasicCondition(keyword, status, category);

        // 페이지 조회 및 생성 공통 메서드 호출
        return fetchPagedProductsWithFetchJoin(builder, pageable);
    }

    @Override
    public Page<ProductWithStatsDto> findAllWithStatsAndWishlist(String keyword, ProductStatus status, Pageable pageable, Long userId) {
        BooleanBuilder builder = createBasicCondition(keyword, status, null);

        return fetchPagedProductsWithStats(builder, pageable, userId);
    }

    @Override
    public Page<ProductWithStatsDto> findByCategoryWithStatsAndWishlist(ProductCategory category, String keyword, ProductStatus status, Pageable pageable, Long userId) {
        // 공통 조건을 사용하여 BooleanBuilder 생성
        BooleanBuilder builder = createBasicCondition(keyword, status, category);

        // 통계 정보를 포함한 페이지 조회 및 생성 메서드 호출
        return fetchPagedProductsWithStats(builder, pageable, userId);
    }

    private Page<ProductWithStatsDto> fetchPagedProductsWithStats(BooleanBuilder builder, Pageable pageable, Long userId) {
        // 페이징된 ID 목록 조회
        List<Long> productIds = queryFactory
                .select(product.id)
                .from(product)
                .where(builder)
                .orderBy(product.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (productIds.isEmpty()) {
            return Page.empty(pageable);
        }

        // ID 목록으로 Product와 images를 Fetch Join하여 조회
        List<Product> products = queryFactory
                .select(product).distinct()
                .from(product)
                .leftJoin(product.images, productImage).fetchJoin()
                .where(product.id.in(productIds))
                .orderBy(product.createdDate.desc())
                .fetch();

        // 조회된 상품들의 조회수 쿼리
        Map<Long ,Long> viewCountMap = queryFactory
                .select(productViewCount.product.id, productViewCount.count)
                .from(productViewCount)
                .where(productViewCount.product.id.in(productIds))
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(productViewCount.product.id),
                        tuple -> tuple.get(productViewCount.count),
                        (a, b) -> a // 중복 키 발생 시 첫 번째 값 유지
                ));

        // 조회된 상품들의 관심 등록 수 조회
        Map<Long, Long> wishlistCountMap = queryFactory
                .select(productWishlist.product.id, productWishlist.count())
                .from(productWishlist)
                .where(productWishlist.product.id.in(productIds))
                .groupBy(productWishlist.product.id)
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(productWishlist.product.id),
                        tuple -> tuple.get(productWishlist.count()),
                        (a, b) -> a)); // 중복 키 발생 시 첫 번째 값 유지

        // 사용자가 관심 등록한 상품 ID 목록 조회
        Map<Long, Boolean> isWishlistedMap; // 기본값은 빈 맵
        if (userId != null) {
            isWishlistedMap = queryFactory
                    .select(productWishlist.product.id)
                    .from(productWishlist)
                    .where(productWishlist.product.id.in(productIds)
                            .and(productWishlist.user.id.eq(userId)))
                    .fetch()
                    .stream()
                    .collect(Collectors.toMap(
                            productId -> productId,
                            productId -> true,
                            (a, b) -> a)); // 중복 키 발생 시 첫 번째 값 유지
        } else {
            isWishlistedMap = Map.of();
        }

        // 조회된 상품을 productIds 순서에 맞게 정렬하고 DTO로 변환
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // 최종 DTO 리스트 생성
        List<ProductWithStatsDto> result = productIds.stream()
                .map(id -> {
                    Product p = productMap.get(id);
                    Long viewCount = viewCountMap.getOrDefault(id, 0L);
                    Long wishlistCount = wishlistCountMap.getOrDefault(id, 0L);
                    boolean isWishlisted = isWishlistedMap.getOrDefault(id, false);

                    return new ProductWithStatsDto(p, viewCount, wishlistCount, isWishlisted);
                })
                .collect(Collectors.toList());

        // 전체 개수 조회를 위한 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(product.count())
                .from(product)
                .where(builder);

        // 페이지 객체 생성 (count 쿼리 최적화)
        return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
    }

    // 상품명 또는 설명에 키워드가 포함된 조건
    private BooleanExpression nameOrDescriptionContains(String keyword) {
        return product.name.containsIgnoreCase(keyword)
                .or(product.description.containsIgnoreCase(keyword));
    }

    // 공통 조건을 사용하여 BooleanBuilder 생성하는 메서드
    private BooleanBuilder createBasicCondition(String keyword, ProductStatus status, ProductCategory category) {
        BooleanBuilder builder = new BooleanBuilder();

        // 카테고리 조건 (선택적)
        if (category != null) {
            builder.and(product.category.eq(category));
        }

        // 키워드 검색 조건 추가
        if (StringUtils.hasText(keyword)) {
            builder.and(nameOrDescriptionContains(keyword));
        }

        // 상품 상태 조건 추가
        if (status != null) {
            builder.and(product.status.eq(status));
        } else {
            // 기본적으로 활성 상품만 조회 (ACTIVE)
            builder.and(product.status.eq(ProductStatus.ACTIVE));
        }

        return builder;
    }

    private Page<Product> fetchPagedProductsWithFetchJoin(BooleanBuilder builder, Pageable pageable) {
        // 페이징된 ID 목록 조회
        List<Long> productIds = queryFactory
                .select(product.id)
                .from(product)
                .where(builder)
                .orderBy(product.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (productIds.isEmpty()) {
            return Page.empty(pageable);
        }

        // ID 목록으로 Product와 images를 Fetch join 하여 조회
        List<Product> content = queryFactory
                .select(product).distinct()
                .from(product)
                .leftJoin(product.images, productImage).fetchJoin()
                .where(product.id.in(productIds))
                .orderBy(product.createdDate.desc())
                .fetch();

        // content 리스트를 productIds 순서에 맞게 재정렬
        Map<Long, Product> productMap = content.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
        List<Product> sortedContent = productIds.stream()
                .map(productMap::get)
                .collect(Collectors.toList());

        // 전체 개수 조회를 위한 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(product.count())
                .from(product)
                .where(builder);

        // 페이지 객체 생성 (count 쿼리 최적화)
        return PageableExecutionUtils.getPage(sortedContent, pageable, countQuery::fetchOne);
    }
}
