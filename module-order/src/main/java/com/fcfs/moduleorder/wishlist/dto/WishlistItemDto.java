package com.fcfs.moduleorder.wishlist.dto;

import com.fcfs.moduleorder.wishlist.entity.WishlistDetail;

public record WishlistItemDto(

        Long productId,
        String productName,
        Integer quantity,
        Integer price
) {
    public static WishlistItemDto from(WishlistDetail wishlistDetail) {
        return new WishlistItemDto(
                wishlistDetail.getProduct().getId(),
                wishlistDetail.getProduct().getName(),
                wishlistDetail.getQuantity(),
                wishlistDetail.getProduct().getPrice()
        );
    }
}
