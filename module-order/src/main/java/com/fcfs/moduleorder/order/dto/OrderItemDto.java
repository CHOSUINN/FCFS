package com.fcfs.moduleorder.order.dto;

import com.fcfs.moduleorder.order.dto.response.ProductResponseDto;

public record OrderItemDto(

        Long productId,
        String productName,
        Integer quantity,
        Integer price
) {
    public static OrderItemDto from(Integer quantity, ProductResponseDto product) {
        return new OrderItemDto(
                product.id(),
                product.name(),
                quantity,
                product.price()
        );
    }
}
