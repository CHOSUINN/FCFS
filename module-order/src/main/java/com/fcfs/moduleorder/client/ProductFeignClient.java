package com.fcfs.moduleorder.client;

import com.fcfs.moduleorder.order.dto.response.ProductResponseDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service"
        , url = "${services.product.url}"
        , fallback = ProductFeignClientFallback.class
)
@CircuitBreaker(name = "module-order-product-circuit-breaker")
@Retry(name = "module-order-product-retry")
public interface ProductFeignClient {

    /** 상품 정보 조회 */
    @GetMapping("/api/products/{productId}")
    ProductResponseDto getProductById(@PathVariable("productId") Long productId);
}
