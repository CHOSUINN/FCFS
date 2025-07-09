package com.fcfs.moduleorder.client;

import com.fcfs.moduleorder.client.dto.PaymentResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "payment-service",
        url = "${services.payment.url}",
        fallback = PaymentFeignClientFallback.class
)
public interface PaymentFeignClient {

    // 결제 호출
    @GetMapping("/api/payments")
    PaymentResult getPaymentResult(@RequestParam Long userId, @RequestParam Long orderId);

}
