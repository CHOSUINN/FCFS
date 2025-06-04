package com.fcfs.fcfs.order.controller;

import com.fcfs.fcfs.global.common.ApiResponse;
import com.fcfs.fcfs.global.security.annotation.UserId;
import com.fcfs.fcfs.order.dto.request.OrderRequestDto;
import com.fcfs.fcfs.order.dto.response.OrderResponseDto;
import com.fcfs.fcfs.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j(topic = "OrderController")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    // 주문 넣기
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponseDto>> create(@UserId Long userId,
                                                                @Valid @RequestBody OrderRequestDto requestDto) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "위시리스트 상품을 주문하는데 성공하였습니다.",
                orderService.createOrder(userId, requestDto)
        ));
    }


    // todo: 주문 취소
    // todo: 주문 전체 조회
    // todo: 주문 단일 조회(주문내역 조회)
    // todo: 반품
}
