package com.fcfs.moduleorder.order.dto.response;

import java.time.LocalDateTime;

public record ProductResponseDto(
        Long id,
        String name,
        String description,
        Integer stock,
        Integer price,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt,
        Long userId
) {}
