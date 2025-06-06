package com.fcfs.moduleuser.wishlist.dto;

import com.fcfs.moduleuser.wishlist.dto.response.ProductResponseDto;

public record WishlistItemDto(

        Long productId,
        String productName,
        Integer quantity,
        Integer price
) {
    public static WishlistItemDto from(ProductResponseDto prduct, Integer quantity) {
        return new WishlistItemDto(
                prduct.id(),
                prduct.name(),
                quantity,
                prduct.price()
        );
    }
}
