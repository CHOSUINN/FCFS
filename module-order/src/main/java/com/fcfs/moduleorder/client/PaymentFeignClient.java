package com.fcfs.moduleorder.client;

import com.fcfs.moduleorder.client.dto.PaymentResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "payment-service",
        url = "${service.payment.url}",
        fallback = PaymentFeignClientFallback.class
)
public interface PaymentFeignClient {

    // 결제 호출
    @GetMapping
    PaymentResult getPaymentResult(Long userId, Long orderId);

}
