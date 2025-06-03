package com.fcfs.fcfs.product.controller;

import com.fcfs.fcfs.global.common.ApiResponse;
import com.fcfs.fcfs.product.dto.response.ProductResponseDto;
import com.fcfs.fcfs.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    // todo: 등록되어 있는 상품 리스트 조회 기능
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponseDto>>> list() {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "상품 전체 목록 조회에 성공하였습니다.",
                productService.listProducts()
        ));
    }

    // todo: 상품 상세 정보 조회 기능
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponseDto>> detail(@PathVariable(name = "productId") Long productId) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "단일 상품 조회에 성공하였습니다.",
                productService.detailProduct(productId)
        ));
    }
}
