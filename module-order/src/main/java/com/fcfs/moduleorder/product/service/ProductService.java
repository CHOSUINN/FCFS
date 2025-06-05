package com.fcfs.moduleorder.product.service;

import com.fcfs.moduleorder.product.dto.response.ProductResponseDto;

import java.util.List;

public interface ProductService {
    List<ProductResponseDto> listProducts();

    ProductResponseDto detailProduct(Long productId);
}
