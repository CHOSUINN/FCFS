package com.microservices.modulepayment.global.client.dto;

public record PaymentResult(
        boolean isSuccess
) {
    public static PaymentResult fail() {
        return new PaymentResult(false);
    }

    public static PaymentResult success() {
        return new PaymentResult(true);
    }

}
