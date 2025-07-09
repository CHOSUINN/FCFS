package com.microservices.modulepayment.payment.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentMethod {

    CARD(0, "카드 결제");

    private final int seq;
    private final String status;
}
