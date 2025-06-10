package com.fcfs.moduleuser.client;

import com.fcfs.moduleuser.wishlist.dto.response.ProductResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

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
}
