package com.fcfs.moduleorder.client;

import com.fcfs.moduleorder.order.dto.response.ProductResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service"
        , url = "${services.product.url}"
        , fallback = ProductFeignClientFallback.class
)
public interface ProductFeignClient {

    /** 상품 정보 조회 */
    @GetMapping("/api/products/{productId}")
    ProductResponseDto getProductById(@PathVariable("productId") Long productId);
}
