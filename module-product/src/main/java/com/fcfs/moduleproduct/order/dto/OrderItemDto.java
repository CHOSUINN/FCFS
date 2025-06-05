package com.fcfs.moduleproduct.order.dto;

import com.fcfs.moduleproduct.product.entity.Product;

public record OrderItemDto(

        Long productId,
        String productName,
        Integer quantity,
        Integer price
) {
    public static OrderItemDto from(Integer quantity, Product product) {
        return new OrderItemDto(
                product.getId(),
                product.getName(),
                quantity,
                product.getPrice()
        );
    }
}
