package com.fcfs.moduleuser.client;

import com.fcfs.moduleuser.wishlist.dto.response.ProductResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service",
        url = "${services.product.url}",
        fallback = ProductFeignFallback.class
)
public interface ProductFeignClient {

    @GetMapping("/api/products/{productId}")
    ProductResponseDto getProductById(@PathVariable("productId") Long productId);
}

