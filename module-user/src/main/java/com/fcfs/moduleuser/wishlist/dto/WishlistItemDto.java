package com.fcfs.moduleuser.wishlist.dto;

import com.fcfs.moduleuser.wishlist.entity.WishlistDetail;

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
