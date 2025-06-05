package com.fcfs.moduleorder.order.dto.response;

import com.fcfs.moduleorder.order.dto.OrderItemDto;
import com.fcfs.moduleorder.order.entity.Order;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDto(

        Long orderId,
        String OrderStatus,
        String address,
        LocalDateTime orderedAt,
        LocalDateTime updatedAt,
        Integer totalPrice,
        List<OrderItemDto> items
) {
    public static OrderResponseDto toDto(Order order, List<OrderItemDto> items, Integer totalPrice) {
        return new OrderResponseDto(
                order.getId(),
                order.getOrderStatus().getStatus(),
                order.getAddress(),
                order.getOrderedAt(),
                order.getUpdatedAt(),
                totalPrice,
                items
        );
    }
}
