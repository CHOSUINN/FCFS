package com.fcfs.moduleorder.order.service;

import com.fcfs.moduleorder.global.exception.CustomException;
import com.fcfs.moduleorder.global.exception.ErrorCode;
import com.fcfs.moduleorder.order.dto.OrderItemDto;
import com.fcfs.moduleorder.order.dto.request.OrderRefundRequestDto;
import com.fcfs.moduleorder.order.dto.request.OrderRequestDto;
import com.fcfs.moduleorder.order.dto.response.OrderResponseDto;
import com.fcfs.moduleorder.order.entity.Order;
import com.fcfs.moduleorder.order.entity.OrderDetail;
import com.fcfs.moduleorder.order.entity.OrderStatus;
import com.fcfs.moduleorder.order.repository.OrderRepository;
import com.fcfs.moduleorder.user.entity.User;
import com.fcfs.moduleorder.user.repository.UserRepository;
import com.fcfs.moduleorder.wishlist.entity.Wishlist;
import com.fcfs.moduleorder.wishlist.entity.WishlistDetail;
import com.fcfs.moduleorder.wishlist.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j(topic = "OrderService")
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;

    public OrderResponseDto createOrder(Long userId, OrderRequestDto requestDto) {

        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        Wishlist wishlist = wishlistRepository.findByUser_id(userId).orElseThrow(
                () -> new CustomException(ErrorCode.WISHLIST_NOT_FOUND)
        );

        List<WishlistDetail> wishlistDetails = wishlist.getWishlistDetail();
        if (wishlistDetails.isEmpty()) {
            throw new CustomException(ErrorCode.ORDER_FAILURE_EMPTY_WISHLIST);
        }

        // WishlistDetail에서 OrderDetail로 변환
        Order order = Order.from(user, requestDto);
        for (WishlistDetail detail : wishlistDetails) {
            OrderDetail orderDetail = OrderDetail.from(detail);
            order.addOrderDetail(orderDetail);
        }

        orderRepository.save(order);
        wishlist.initWishlist();


        return toResponseDto(order);
    }

    public void cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new CustomException(ErrorCode.ORDER_NOT_FOUND)
        );
        if (!order.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.ORDER_FORBIDDEN);
        }

        if (!order.getOrderStatus().isCancellable()) {
            throw new CustomException(ErrorCode.ORDER_CANCELLATION_FAILURE);
        }
        order.setOrderStatus(OrderStatus.ORDER_CANCELED);
    }

    public List<OrderResponseDto> listOrder(Long userId) {
        List<Order> orders = orderRepository.findAllByUserId(userId);
        List<OrderResponseDto> orderResponseDtos = new ArrayList<>();
        for (Order order : orders) {
            OrderResponseDto orderResponseDto = toResponseDto(order);
            orderResponseDtos.add(orderResponseDto);
        }
        return orderResponseDtos;
    }

    public List<OrderResponseDto> listOrderByStatus(Long userId, int seq) {
        OrderStatus status = OrderStatus.getOrderStatus(seq);

        List<Order> orders = orderRepository.findAllByUserIdAndOrderStatus(userId, status);

        List<OrderResponseDto> orderResponseDtos = new ArrayList<>();
        for (Order order : orders) {
            orderResponseDtos.add(toResponseDto(order));
        }
        return orderResponseDtos;
    }


    public OrderResponseDto detailOrder(Long userId, Long orderId) {
        Order order = orderRepository.findByUserIdAndId(userId, orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        return toResponseDto(order);
    }

    public void refundProduct(Long userId, Long orderId, OrderRefundRequestDto requestDto) {
        // 1. 주문 조회 (userId + orderId)
        Order order = orderRepository.findByUserIdAndId(userId, orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        // 2. 권한 검증: 해당 주문의 소유자가 맞는지 확인
        if (!order.getUser().getId().equals(userId)) {
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

    private OrderResponseDto toResponseDto(Order order) {
        int totalPrice = 0;
        List<OrderItemDto> items = new ArrayList<>();
        for (OrderDetail detail : order.getOrderDetails()) {
            OrderItemDto item = OrderItemDto.from(detail.getQuantity(), detail.getProduct());
            items.add(item);
            totalPrice += (detail.getProduct().getPrice() * detail.getQuantity());
        }
        return OrderResponseDto.toDto(order, items, totalPrice);
    }
}
