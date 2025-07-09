package com.microservices.modulepayment.payment.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {
    PENDING(0, "결제 중"),
    APPROVED(1, "결제 승인"),
    REJECTED(2, "결제 거부");

    private final int seq;
    private final String status;

}
