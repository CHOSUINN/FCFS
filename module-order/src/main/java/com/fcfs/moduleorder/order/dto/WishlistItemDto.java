package com.fcfs.moduleorder.order.dto;

public record WishlistItemDto(

        Long productId,
        String productName,
        Integer quantity,
        Integer price
) {

}
