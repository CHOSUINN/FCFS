package com.fcfs.moduleuser.order.dto.request;

import jakarta.validation.constraints.NotNull;

public record OrderRequestDto(

        @NotNull(message = "배송지는 반드시 입력해야합니다.")
        String address
) {}
