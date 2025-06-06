package com.fcfs.moduleuser.client;

import com.fcfs.moduleuser.wishlist.dto.response.ProductResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "product-service", url = "http://localhost:8082")
public interface ProductClient {

    @GetMapping("api/products/{productId}")
    ProductResponseDto getProductById(@PathVariable("productId") Long productId);

    @GetMapping("/products")
    List<ProductResponseDto> getProducts();
}
