package com.fcfs.moduleorder.order.service;

import com.fcfs.moduleorder.order.dto.request.OrderRequestDto;
import com.fcfs.moduleorder.order.dto.response.OrderResponseDto;

import java.util.List;

public interface OrderService {
    OrderResponseDto createOrder(Long userId, OrderRequestDto requestDto);

    void cancelOrder(Long userId, Long orderId);

    List<OrderResponseDto> listOrder(Long userId);

    List<OrderResponseDto> listOrderByStatus(Long userId, int seq);

    OrderResponseDto detailOrder(Long userId, Long orderId);

    void refundProduct(Long userId, Long orderId);
}
