package com.fcfs.fcfs.wishlist.dto.response;

import com.fcfs.fcfs.wishlist.dto.WishlistItemDto;
import com.fcfs.fcfs.wishlist.entity.Wishlist;

import java.time.LocalDateTime;
import java.util.List;

public record WishlistResponseDto(

        Long wishlistId,
        Long userId,
        Integer totalPrice,
        LocalDateTime modifiedAt,
        List<WishlistItemDto> items
) {
    public static WishlistResponseDto toDto(Wishlist wishlist, List<WishlistItemDto> itemDtos, Integer totalPrice) {
        return new WishlistResponseDto(
                wishlist.getId(),
                wishlist.getUser().getId(),
                totalPrice,
                wishlist.getModifiedAt(),
                itemDtos
        );
    }
}
