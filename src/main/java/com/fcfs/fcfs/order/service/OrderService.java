package com.fcfs.fcfs.order.service;

import com.fcfs.fcfs.global.exception.CustomException;
import com.fcfs.fcfs.global.exception.ErrorCode;
import com.fcfs.fcfs.order.dto.OrderItemDto;
import com.fcfs.fcfs.order.dto.request.OrderRequestDto;
import com.fcfs.fcfs.order.dto.response.OrderResponseDto;
import com.fcfs.fcfs.order.entity.Order;
import com.fcfs.fcfs.order.entity.OrderDetail;
import com.fcfs.fcfs.order.repository.OrderRepository;
import com.fcfs.fcfs.product.entity.Product;
import com.fcfs.fcfs.user.entity.User;
import com.fcfs.fcfs.user.repository.UserRepository;
import com.fcfs.fcfs.wishlist.entity.Wishlist;
import com.fcfs.fcfs.wishlist.entity.WishlistDetail;
import com.fcfs.fcfs.wishlist.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


        return toReponseDto(order);
    }

    private OrderResponseDto toReponseDto(Order order) {
        Integer totalPrice = 0;
        List<OrderItemDto> items = new ArrayList<>();
        for (OrderDetail detail : order.getOrderDetails()) {
            OrderItemDto item = OrderItemDto.from(detail.getQuantity(), detail.getProduct());
            items.add(item);
            totalPrice += (detail.getProduct().getPrice() * detail.getQuantity());
        }
        return OrderResponseDto.toDto(order, items, totalPrice);
    }
}
