package com.fcfs.moduleuser.client;

import com.fcfs.moduleuser.wishlist.dto.response.ProductResponseDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "product-service"
        , url = "${services.product.url}"
        , fallback = ProductFeignFallback.class
)
@CircuitBreaker(name = "module-user-product-circuit-breaker")
@Retry(name = "module-user-product-retry")
public interface ProductFeignClient {

    @GetMapping("/api/products/{productId}")
    ProductResponseDto getProductById(@PathVariable("productId") Long productId);

}


