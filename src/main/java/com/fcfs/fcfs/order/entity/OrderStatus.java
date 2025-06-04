package com.fcfs.fcfs.order.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {

    PREPARING_PRODUCT(0, "상품 준비 중"),
    PREPARING_DELIVERING(1, "배송 준비 중"),
    DELIVERING(2, "배송 중"),
    DELIVERED(3, "배송 완료"),
    ORDER_CANCELED(4, "주문 취소"),
    CANCELLATION_COMPLETED(5, "취소 완료"),
    RETURN_REQUESTED(6, "반품 신청"),
    RETURN_COMPLETED(7, "반품 완료");

    private final int seq;
    private final String status;

    public boolean isCancellable() {
        return this.seq < DELIVERING.seq;
    }

    public static OrderStatus getOrderStatus(int seq) {
        return OrderStatus.values()[seq];
    }

    public boolean isReturnableStatus() {
        return this == DELIVERED;
    }
}
