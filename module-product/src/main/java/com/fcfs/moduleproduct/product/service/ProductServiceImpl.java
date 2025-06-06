package com.fcfs.moduleproduct.product.service;

import com.fcfs.moduleproduct.global.exception.CustomException;
import com.fcfs.moduleproduct.global.exception.ErrorCode;
import com.fcfs.moduleproduct.product.dto.response.ProductResponseDto;
import com.fcfs.moduleproduct.product.entity.Product;
import com.fcfs.moduleproduct.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j(topic = "ProductServiceImpl")
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public List<ProductResponseDto> listProducts() {
        List<Product> products = productRepository.findAll();

        List<ProductResponseDto> list = new ArrayList<>();
        for (Product product : products) {
            list.add(ProductResponseDto.toDto(product));
        }
        return list;
    }

    @Override
    public ProductResponseDto detailProduct(Long productId) {
        return ProductResponseDto.toDto(
                productRepository.findById(productId).orElseThrow(
                        () -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND)
                )
        );
    }
}
