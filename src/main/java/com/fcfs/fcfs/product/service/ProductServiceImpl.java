package com.fcfs.fcfs.product.service;

import com.fcfs.fcfs.product.dto.response.ProductResponseDto;
import com.fcfs.fcfs.product.entity.Product;
import com.fcfs.fcfs.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
                        () -> new IllegalArgumentException("존재하지 않는 상품입니다.")
                )
        );
    }
}
