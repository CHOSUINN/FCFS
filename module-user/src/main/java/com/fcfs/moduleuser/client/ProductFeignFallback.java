package com.fcfs.moduleuser.client;

import com.fcfs.moduleuser.global.exception.CustomException;
import com.fcfs.moduleuser.global.exception.ErrorCode;
import com.fcfs.moduleuser.wishlist.dto.response.ProductResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j(topic = "ProductFeignFallback")
@Component
public class ProductFeignFallback implements ProductFeignClient {

    @Override
    public ProductResponseDto getProductById(Long productId) {
        log.warn("[Fallback] getProductById: productId={}, 서비스 장애!", productId);
        throw new CustomException(ErrorCode.FEIGN_ERROR);
    }
}
