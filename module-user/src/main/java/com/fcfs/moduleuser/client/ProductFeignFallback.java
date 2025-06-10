package com.fcfs.moduleuser.client;

import com.fcfs.moduleuser.wishlist.dto.response.ProductResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j(topic = "ProductFeignFallback")
@Component
public class ProductFeignFallback implements ProductFeignClient {

    @Override
    public ProductResponseDto getProductById(Long productId) {
        log.warn("[Fallback] getProductById: productId={}, 서비스 장애!", productId);
        // 장애 안내용 더미 데이터
        return new ProductResponseDto(
                -1L,
                "[서비스 장애] 상품 정보를 불러올 수 없습니다.",
                "",
                0,
                0,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
    }

    @Override
    public List<ProductResponseDto> getProducts() {
        log.warn("[Fallback] 상품 호출 서비스 장애!");
        // 더미 데이터 리스트(혹은 아예 빈 리스트 반환)
        ProductResponseDto fallbackProduct = new ProductResponseDto(
                -1L,
                "[서비스 장애] 상품 목록을 불러올 수 없습니다.",
                "",
                0,
                0,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
        return List.of(fallbackProduct);
        // 또는 return List.of();   ← 정말 아무것도 안 주고 싶을 때
    }
}
