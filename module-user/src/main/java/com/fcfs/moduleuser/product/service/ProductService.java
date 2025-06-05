package com.fcfs.moduleuser.product.service;

import com.fcfs.moduleuser.product.dto.response.ProductResponseDto;

import java.util.List;

public interface ProductService {
    List<ProductResponseDto> listProducts();

    ProductResponseDto detailProduct(Long productId);
}
