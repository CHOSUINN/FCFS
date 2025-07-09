package com.microservices.modulepayment.payment.service;

import com.microservices.modulepayment.global.client.dto.PaymentResult;
import com.microservices.modulepayment.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "PaymentService")
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl {

    private final PaymentRepository paymentRepository;

    // 결제 시도
    public PaymentResult payment(Long userId, Long orderId) {
        // 결제 생성

        // 각 페이지별 이탈
        // 이탈할 때마다 실패도 저장.

        // 최종적으로 이탈하지 않은 사람들의 한정에서
        // 주문이 들어감. 
        return null;
    }
}
