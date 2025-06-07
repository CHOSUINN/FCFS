package com.fcfs.moduleorder.order.service;

import com.fcfs.moduleorder.client.ProductFeignClient;
import com.fcfs.moduleorder.client.UserFeignClient;
import com.fcfs.moduleorder.global.exception.CustomException;
import com.fcfs.moduleorder.global.exception.ErrorCode;
import com.fcfs.moduleorder.order.dto.OrderItemDto;
import com.fcfs.moduleorder.order.dto.WishlistItemDto;
import com.fcfs.moduleorder.order.dto.request.OrderRequestDto;
import com.fcfs.moduleorder.order.dto.response.OrderResponseDto;
import com.fcfs.moduleorder.order.dto.response.ProductResponseDto;
import com.fcfs.moduleorder.order.dto.response.UserEntityResponseDto;
import com.fcfs.moduleorder.order.dto.response.WishlistResponseDto;
import com.fcfs.moduleorder.order.entity.Order;
import com.fcfs.moduleorder.order.entity.OrderItem;
import com.fcfs.moduleorder.order.entity.OrderStatus;
import com.fcfs.moduleorder.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j(topic = "OrderServiceImpl")
@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserFeignClient userFeignClient;
    private final ProductFeignClient productFeignClient;

    @Override
    public OrderResponseDto createOrder(Long userId, OrderRequestDto requestDto) {

        UserEntityResponseDto user = userFeignClient.getUserEntity(userId);
        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        WishlistResponseDto wishlist = userFeignClient.getWishlistEntity(userId);
        if (wishlist == null) {
            throw new CustomException(ErrorCode.ORDER_FAILURE_EMPTY_WISHLIST);
        }

        Order order = Order.from(userId, requestDto);
        for (WishlistItemDto wd : wishlist.items()) {
            order.addOrderDetail(OrderItem.from(wd));
        }

        orderRepository.save(order);
        userFeignClient.clearWishlist(userId);

        return toResponseDto(order);
    }

    @Override
    public void cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new CustomException(ErrorCode.ORDER_NOT_FOUND)
        );
        if (!order.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.ORDER_FORBIDDEN);
        }

        if (!order.getOrderStatus().isCancellable()) {
            throw new CustomException(ErrorCode.ORDER_CANCELLATION_FAILURE);
        }
        order.setOrderStatus(OrderStatus.ORDER_CANCELED);
    }

    @Override
    public List<OrderResponseDto> listOrder(Long userId) {
        List<Order> orders = orderRepository.findAllByUserId(userId);
        List<OrderResponseDto> dtos = new ArrayList<>();
        for (Order o : orders) {
            dtos.add(toResponseDto(o));
        }
        return dtos;
    }

    @Override
    public List<OrderResponseDto> listOrderByStatus(Long userId, int seq) {
        OrderStatus status = OrderStatus.getOrderStatus(seq);
        List<Order> orders = orderRepository.findAllByUserIdAndOrderStatus(userId, status);
        List<OrderResponseDto> dtos = new ArrayList<>();
        for (Order o : orders) {
            dtos.add(toResponseDto(o));
        }
        return dtos;
    }


    @Override
    public OrderResponseDto detailOrder(Long userId, Long orderId) {
        Order order = orderRepository.findByUserIdAndId(userId, orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
        return toResponseDto(order);
    }

    @Override
    public void refundProduct(Long userId, Long orderId) {
        // 1. 주문 조회 (userId + orderId)
        Order order = orderRepository.findByUserIdAndId(userId, orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        // 2. 권한 검증: 해당 주문의 소유자가 맞는지 확인
        if (!order.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.ORDER_FORBIDDEN);
        }

        // 3. 현재 상태가 "DELIVERED"인지 확인
        if (!order.getOrderStatus().isReturnableStatus()) {
            // 이미 반품 요청이 되었거나 배송 중/준비 중 등인 경우
            throw new CustomException(ErrorCode.ORDER_RETURN_NOT_ALLOWED);
        }

        // 4. "DELIVERED" 시점을 판단 (updatedAt 사용).
        LocalDateTime deliveredAt = order.getUpdatedAt();
        LocalDateTime now = LocalDateTime.now();

        // 5. 7일(168시간) 이내인지 확인
        if (deliveredAt.plusDays(7).isBefore(now)) {
            throw new CustomException(ErrorCode.ORDER_RETURN_EXPIRED);
        }

        // 6. 반품 신청 처리: 상태를 RETURN_REQUESTED로 변경
        order.setOrderStatus(OrderStatus.RETURN_REQUESTED);
    }

    /** 주문 ↔ 응답 DTO 변환
     *  각 OrderItem 에 대해 ProductFeignClient 로 가격·이름 조회 후 DTO 생성 */
    private OrderResponseDto toResponseDto(Order order) {
        int totalPrice = 0;
        List<OrderItemDto> items = new ArrayList<>();

        for (OrderItem detail : order.getOrderDetails()) {
            // 원격 상품 서비스 호출
            ProductResponseDto prod = productFeignClient.getProduct(detail.getProductId());
            OrderItemDto itemDto = OrderItemDto.from(detail.getQuantity(), prod);
            items.add(itemDto);

            totalPrice += prod.price() * detail.getQuantity();
        }
        return OrderResponseDto.toDto(order, items, totalPrice);
    }
}
