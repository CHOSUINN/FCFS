package com.fcfs.moduleproduct.product.controller;

import com.fcfs.moduleproduct.product.dto.response.ProductResponseDto;
import com.fcfs.moduleproduct.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j(topic = "ProductController")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    // 등록되어 있는 상품 리스트 조회 기능
    @GetMapping
    public List<ProductResponseDto> list() {
        return productService.listProducts();
    }

    // 상품 상세 정보 조회 기능
    @GetMapping("/{productId}")
    public ProductResponseDto detail(@PathVariable(name = "productId") Long productId) {
        return productService.detailProduct(productId);
    }
}
