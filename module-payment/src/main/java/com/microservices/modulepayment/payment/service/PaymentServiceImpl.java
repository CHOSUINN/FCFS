package com.microservices.modulepayment.payment.service;

import com.microservices.modulepayment.global.client.dto.PaymentResult;
import com.microservices.modulepayment.payment.entity.Payment;
import com.microservices.modulepayment.payment.entity.PaymentStatus;
import com.microservices.modulepayment.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j(topic = "PaymentService")
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl {

    private final PaymentRepository paymentRepository;

    // 결제 시도
    @Transactional
    public PaymentResult payment(Long userId, Long orderId) {

        // 결제 생성
        Payment payment = Payment.from(userId, orderId);
        paymentRepository.save(payment);

        // 1차 이탈 가능 위치. 20% 확률로 "결제 버튼 클릭 안함"
        int firstDrop = ThreadLocalRandom.current().nextInt(100);
        if (firstDrop < 20) {
            // 결제 시작조차 하지 않은 이탈 시나리오
            log.info("결제 화면 진입 후 결제 시도 없이 이탈: orderId={}, userId={}", orderId, userId);
            payment.changeStatus(PaymentStatus.FAIL);
            paymentRepository.save(payment);

            return PaymentResult.fail();
        }

        // 2차 이탈 가능 위치. 20% 확률로 "결제 중 사용자 실패"
        int secondDrop = ThreadLocalRandom.current().nextInt(100);
        if (secondDrop < 20) {
            // 결제 시도 후 사용자 사유로 실패 시나리오
            log.info("결제 시도 후 사용자 사유로 실패: orderId={}, userId={}", orderId, userId);
            payment.changeStatus(PaymentStatus.FAIL);
            paymentRepository.save(payment);

            return PaymentResult.fail();
        }

        // 결제 성공
        log.info("결제 성공: orderId={}, userId={}", orderId, userId);
        payment.changeStatus(PaymentStatus.APPROVED);
        paymentRepository.save(payment);

        return PaymentResult.success();

    }

}
