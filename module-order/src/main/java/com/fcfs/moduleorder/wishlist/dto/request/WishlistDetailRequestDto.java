package com.fcfs.moduleorder.wishlist.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record WishlistDetailRequestDto(

        @NotNull(message = "상품 아이디는 필수 입력 사항입니다.")
        Long productId,

        @NotNull(message = "상품 수량은 필수 입력 사항입니다.")
        @Min(value = 1, message = "수량은 0이상만 입력이 가능합니다.")
        Integer quantity
) {}
