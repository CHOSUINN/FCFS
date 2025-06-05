package com.fcfs.moduleproduct.product.service;

import com.fcfs.moduleproduct.product.dto.response.ProductResponseDto;

import java.util.List;

public interface ProductService {
    List<ProductResponseDto> listProducts();

    ProductResponseDto detailProduct(Long productId);
}
