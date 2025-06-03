package com.fcfs.fcfs.product.service;

import com.fcfs.fcfs.product.dto.response.ProductResponseDto;

import java.util.List;

public interface ProductService {
    List<ProductResponseDto> listProducts();

    ProductResponseDto detailProduct(Long productId);
}
