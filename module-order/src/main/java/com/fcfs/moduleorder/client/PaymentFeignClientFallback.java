package com.fcfs.moduleorder.client;

import com.fcfs.moduleorder.client.dto.PaymentResult;
import com.fcfs.moduleorder.global.exception.CustomException;
import com.fcfs.moduleorder.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j(topic = "PaymentFeignClientFallback")
@Component
public class PaymentFeignClientFallback implements PaymentFeignClient {

    @Override
    public PaymentResult getPaymentResult(Long userId, Long id) {
        log.warn("[Fallback] 서비스 장애!");
        throw new CustomException(ErrorCode.FEIGN_ERROR);
    }
}
