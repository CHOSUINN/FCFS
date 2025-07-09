package com.fcfs.moduleorder.order.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public enum OrderStatus {

    PENDING(0, "주문 진행 중"), // 주문 생성 후 결제 진행
    PREPARING_PRODUCT(1, "상품 준비 중"),
    PREPARING_DELIVERING(2, "배송 준비 중"),
    DELIVERING(3, "배송 중"),
    DELIVERED(4, "배송 완료"),
    ORDER_CANCELED(5, "주문 취소"),
    CANCELLATION_COMPLETED(6, "취소 완료"),
    RETURN_REQUESTED(7, "반품 신청"),
    RETURN_COMPLETED(8, "반품 완료");

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

    public OrderStatus next() {
        int nextSeq = this.seq + 1;
        for (OrderStatus status : OrderStatus.values()) {
            if (status.seq == nextSeq) {
                return status;
            }
        }
        throw new IllegalStateException("더 이상 다음 상태가 없습니다.");
    }

}
