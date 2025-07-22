package com.fcfs.moduleorder.order.service;

import com.fcfs.moduleorder.client.PaymentFeignClient;
import com.fcfs.moduleorder.client.ProductFeignClient;
import com.fcfs.moduleorder.client.UserFeignClient;
import com.fcfs.moduleorder.client.dto.PaymentResult;
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
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
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
    private final PaymentFeignClient paymentFeignClient;
    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @Override
    public Mono<OrderResponseDto> createOrder(Long userId, OrderRequestDto requestDto) {
        // 동기 외부 서비스 호출부는 리액티브 전환이 어렵지만, Redis 영역만 리액티브로 처리
        UserEntityResponseDto user = userFeignClient.getUserEntity(userId);
        if (user == null) {
            return Mono.error(new CustomException(ErrorCode.USER_NOT_FOUND));
        }

        WishlistResponseDto wishlist = userFeignClient.getWishlistEntity(userId);
        if (wishlist == null) {
            return Mono.error(new CustomException(ErrorCode.ORDER_FAILURE_EMPTY_WISHLIST));
        }

        Order order = Order.from(userId, requestDto);
        for (WishlistItemDto wd : wishlist.items()) {
            order.addOrderDetail(OrderItem.from(wd));
        }

        orderRepository.save(order);

        return reserveStock(order.getOrderDetails(), order.getUserId())
                .flatMap(reserveOk -> {
                    if (!reserveOk) {
                        return Mono.error(new CustomException(ErrorCode.OUT_OF_STOCK));
                    }
                    PaymentResult paymentResult = paymentFeignClient.getPaymentResult(userId, order.getId());
                    if (paymentResult.isSuccess()) {
                        order.setOrderStatus(order.getOrderStatus().next());
                        userFeignClient.clearWishlist(userId);
                    } else {
                        order.setOrderStatus(OrderStatus.ORDER_CANCELED);
                        // 결제 실패시 선점 재고 복구
                        return releaseStock(order.getOrderDetails(), userId)
                                .then(Mono.just(toResponseDto(order)));
                    }
                    return Mono.just(toResponseDto(order));
                });
    }

    // 재고 선점 (리스트 버전)
    private Mono<Boolean> reserveStock(List<OrderItem> orderDetails, Long userId) {
        // 모든 재고 선점이 성공해야 함
        return Flux.fromIterable(orderDetails)
                .concatMap(detail ->
                        tryReserveStock(detail.getProductId(), userId, detail.getQuantity())
                                .flatMap(success -> {
                                    if (!success) {
                                        // 롤백: 이미 성공한 것 release
                                        return releaseStock(orderDetails, userId)
                                                .then(Mono.just(false));
                                    }
                                    return Mono.just(true);
                                })
                )
                .all(result -> result); // 모두 true여야 true 반환
    }

    // 재고 선점 (단일)
    public Mono<Boolean> tryReserveStock(Long productId, Long userId, int quantity) {
        String stockKey = "product:" + productId + ":stock";
        String userReserveKey = "reserve:" + productId + ":" + userId;

        return reactiveRedisTemplate.opsForValue().get(stockKey)
                .map(stockStr -> stockStr == null ? 0 : Integer.parseInt(stockStr))
                .flatMap(currentStock -> {
                    if (currentStock < quantity) return Mono.just(false);

                    // 재고 차감
                    return reactiveRedisTemplate.opsForValue().decrement(stockKey, quantity)
                            .flatMap(newStock -> {
                                if (newStock == null || newStock < 0) {
                                    // 초과 차감 복구
                                    return reactiveRedisTemplate.opsForValue().increment(stockKey, quantity)
                                            .thenReturn(false);
                                }
                                // 유저별 예약정보 3분간 저장
                                return reactiveRedisTemplate.opsForValue()
                                        .set(userReserveKey, String.valueOf(quantity), Duration.ofSeconds(180))
                                        .thenReturn(true);
                            });
                });
    }

    // 재고 롤백 (복구)
    public Mono<Void> releaseStock(List<OrderItem> orderDetails, Long userId) {
        return Flux.fromIterable(orderDetails)
                .flatMap(item -> releaseStock(item.getProductId(), userId, item.getQuantity()))
                .then();
    }

    public Mono<Void> releaseStock(Long productId, Long userId, int quantity) {
        String stockKey = "product:" + productId + ":stock";
        String userReserveKey = "reserve:" + productId + ":" + userId;
        return reactiveRedisTemplate.opsForValue().increment(stockKey, quantity)
                .then(reactiveRedisTemplate.delete(userReserveKey).then());
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
            ProductResponseDto prod = productFeignClient.getProductById(detail.getProductId());

            // fallback 함수 예외처리
            if (prod == null) {
                throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND, "상품 정보를 불러올 수 없습니다. (id=" + detail.getProductId() + ")");
            } else if (prod.price() == -1L) {
                throw new CustomException(ErrorCode.FEIGN_ERROR);
            }

            OrderItemDto itemDto = OrderItemDto.from(detail.getQuantity(), prod);
            items.add(itemDto);

            totalPrice += prod.price() * detail.getQuantity();
        }
        return OrderResponseDto.toDto(order, items, totalPrice);
    }
}
