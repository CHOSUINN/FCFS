package com.fcfs.fcfs.product.dto.response;

import com.fcfs.fcfs.product.entity.Product;

import java.time.LocalDateTime;

public record ProductResponseDto(
        Long id,
        String name,
        String description,
        Integer stock,
        Integer price,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt,
        Long userId
) {
    public static ProductResponseDto toDto(Product product) {
        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getStock(),
                product.getPrice(),
                product.getCreatedAt(),
                product.getModifiedAt(),
                product.getUser().getId()
        );
    }
}
