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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j(topic = "OrderServiceImpl")
@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserFeignClient userFeignClient;
    private final ProductFeignClient productFeignClient;
    private final PaymentFeignClient paymentFeignClient;
    private final RedisTemplate<String, String> redisTemplate;

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

        // 주문 생성
        orderRepository.save(order);

        // 결제 직전에 재고를 미리 선점해둔다.
        reserveStock(order.getOrderDetails(), order.getUserId());

        log.info("결제직전까지는 문제 없음");
        // 결제 api 호출. 결제 실패 혹은 성공 결과 반환
        PaymentResult paymentResult = paymentFeignClient.getPaymentResult(userId, order.getId());

        // 결제 완료되면 주문 상태 변경.
        // 실패시 그대로 취소
        if (paymentResult.isSuccess()) {
            // 성공시 상품 준비중으로 변경 후 위시리스트 초기화
            order.setOrderStatus(order.getOrderStatus().next());
            userFeignClient.clearWishlist(userId);
        } else {
            order.setOrderStatus(OrderStatus.ORDER_CANCELED);
            // 선점 재고 복구
        }

        return toResponseDto(order);
    }

    private void reserveStock(List<OrderItem> orderDetails, Long userId) {
        List<OrderItem> reserved = new ArrayList<>();
        for (OrderItem detail : orderDetails) {
            boolean ok = tryReserveStock(detail.getProductId(), userId, detail.getQuantity());
            if (!ok) {
                // 이미 선점한 것 롤백
                for (OrderItem rollback : reserved) {
                    releaseStock(rollback.getProductId(), userId, rollback.getQuantity());
                }
                throw new CustomException(ErrorCode.OUT_OF_STOCK, "상품 재고 부족");
            }
            reserved.add(detail);
        }
    }

    public boolean tryReserveStock(Long productId, Long userId, int quantity) {
        String stockKey = "product:" + productId + ":stock";
        String userReserveKey = "reserve:" + productId + ":" + userId;

        // 1. 재고 먼저 확인
        String currentStockStr = redisTemplate.opsForValue().get(stockKey);
        int currentStock = currentStockStr == null ? 0 : Integer.parseInt(currentStockStr);

        if (currentStock < quantity) {
            // 재고 부족
            return false;
        }

        // 2. 재고 차감
        Long newStock = redisTemplate.opsForValue().decrement(stockKey, quantity);
        if (newStock == null || newStock < 0) {
            // 재고 초과 차감된 경우 복구
            redisTemplate.opsForValue().increment(stockKey, quantity);
            return false;
        }

        // 3. 유저별 예약 정보 (선택) - 3분 타임아웃
        redisTemplate.opsForValue().set(userReserveKey, String.valueOf(quantity), 180, TimeUnit.SECONDS);

        return true;
    }


    // 결제 실패 혹은 이탈 시 재고 복구
    public void releaseStock(Long productId, Long userId, int quantity) {
        String stockKey = "product:" + productId + ":stock";
        String userReserveKey = "reserve:" + productId + ":" + userId;
        redisTemplate.opsForValue().increment(stockKey, quantity);
        redisTemplate.delete(userReserveKey);
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
