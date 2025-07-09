package com.microservices.modulepayment.payment.controller;

import com.microservices.modulepayment.global.client.dto.PaymentResult;
import com.microservices.modulepayment.payment.service.PaymentServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j(topic = "PaymentController")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentServiceImpl paymentService;

    // 위시리스트 -> 주문하기(지금 이 api 호출) -> 주문넣기(orderAPI 호출)
//    @PostMapping
//    public ResponseEntity<ApiResponse<PaymentResponseDto>> payment(@RequestBody PaymentRequestDto requestDto) {
//        log.info("Payment request: {}", requestDto);
//        return ResponseEntity.ok(ApiResponse.success(
//                HttpStatus.OK,
//                "결제에 성공하였습니다.",
//                paymentService.payment(requestDto)
//        ));
//    }

    // 주문 생성시 호출되는 결제 api
    @GetMapping
    public PaymentResult payment(@RequestParam(name = "userId") Long userId,
                                 @RequestParam(name = "orderId") Long orderId
    ) {
        log.info("payment request: {}, {}", userId, orderId);
        return paymentService.payment(userId, orderId);
    }
}
