package com.fcfs.moduleorder.client;

import com.fcfs.moduleorder.order.dto.response.ProductResponseDto;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class ProductFeignClientFallback implements ProductFeignClient {
    @Override
    public ProductResponseDto getProductById(Long productId) {
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