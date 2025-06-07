package com.fcfs.moduleorder.order.dto.response;

import com.fcfs.moduleorder.order.dto.WishlistItemDto;

import java.time.LocalDateTime;
import java.util.List;

public record WishlistResponseDto(

        Long wishlistId,
        Long userId,
        Integer totalPrice,
        LocalDateTime modifiedAt,
        List<WishlistItemDto> items
) {}
