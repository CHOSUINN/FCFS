package com.microservices.modulepayment.payment.dto.request;

public record PaymentRequestDto(
        Integer paymentMethodSeq,
        String address,
        Integer totalPrice
) {}
