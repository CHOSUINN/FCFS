package com.fcfs.fcfs.order.dto.request;

import jakarta.validation.constraints.NotNull;

public record OrderRefundRequestDto(

        @NotNull(message = "반품할 상품의 Id를 입력해야합니다.")
        Long productId
) {}
