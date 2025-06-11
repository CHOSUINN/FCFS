package com.fcfs.moduleorder.client;

import com.fcfs.moduleorder.global.exception.CustomException;
import com.fcfs.moduleorder.global.exception.ErrorCode;
import com.fcfs.moduleorder.order.dto.response.ProductResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j(topic = "ProductFeignClientFallback")
@Component
public class ProductFeignClientFallback implements ProductFeignClient {
    @Override
    public ProductResponseDto getProductById(Long productId) {
        log.warn("[Fallback] getProductById: productId={}, 서비스 장애!", productId);
        throw new CustomException(ErrorCode.FEIGN_ERROR);
    }
} 