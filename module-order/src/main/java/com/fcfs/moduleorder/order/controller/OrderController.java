package com.fcfs.moduleorder.order.controller;

import com.fcfs.moduleorder.global.common.ApiResponse;
import com.fcfs.moduleorder.order.dto.request.OrderRequestDto;
import com.fcfs.moduleorder.order.dto.response.OrderResponseDto;
import com.fcfs.moduleorder.order.entity.OrderStatus;
import com.fcfs.moduleorder.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j(topic = "OrderController")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    // 주문 넣기
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponseDto>> create(@RequestHeader("X-USER-ID") Long userId,
                                                                @Valid @RequestBody OrderRequestDto requestDto) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "위시리스트 상품을 주문하는데 성공하였습니다.",
                orderService.createOrder(userId, requestDto)
        ));
    }

    // 주문 취소
    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> cancel(@RequestHeader("X-USER-ID") Long userId,
                                 @PathVariable(name = "orderId") Long orderId) {
        orderService.cancelOrder(userId, orderId);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "주문을 취소하였습니다."
        ));
    }

    // 주문 전체 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponseDto>>> list(@RequestHeader("X-USER-ID") Long userId) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "주문 목록을 조회하는데 성공하였습니다.",
                orderService.listOrder(userId)
        ));
    }

    // 주문 단일 조회(주문내역 조회)
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponseDto>> detail(@RequestHeader("X-USER-ID") Long userId,
                                                                @PathVariable(name = "orderId") Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "주문을 조회하는데 성공하였습니다.",
                orderService.detailOrder(userId, orderId)
        ));
    }

    // 주문 상태별 조회
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<List<OrderResponseDto>>> listStatus(@RequestHeader("X-USER-ID") Long userId,
                                                                          @RequestParam(name = "seq")int seq) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "'" + OrderStatus.getOrderStatus(seq) + "'별 목록 조회하는데 성공하였습니다.",
                orderService.listOrderByStatus(userId, seq)
        ));
    }

    // Todo: 지금은 주문 전체 반품인데, 특정 물품만 반품하게 변경
    // 주문내역 통째로 반품
    @PostMapping("/{orderId}/return")
    public ResponseEntity<?> refund(@RequestHeader("X-USER-ID") Long userId,
                                 @PathVariable(name = "orderId") Long orderId
    ) {
        orderService.refundProduct(userId, orderId);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "반품이 되었습니다."
        ));
    }
}
