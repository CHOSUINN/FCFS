package com.fcfs.moduleuser.user.controller;

import com.fcfs.moduleuser.client.ProductFeignClient;
import com.fcfs.moduleuser.wishlist.dto.response.ProductResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestCircuitController {

    private final ProductFeignClient productFeignClient;

    public TestCircuitController(ProductFeignClient productFeignClient) {
        this.productFeignClient = productFeignClient;
    }

    @GetMapping("/test/circuit")
    public String testCircuit() {
        StringBuilder result = new StringBuilder();
        for (int i = 1; i <= 10; i++) {
            try {
                ProductResponseDto dto = productFeignClient.getProductById((long) i); // 이 id는 아무거나 넣어도 됨
                result.append("[").append(i).append("] success: ").append(dto).append("<br/>");
            } catch (Exception e) {
                result.append("[").append(i).append("] fail: ").append(e.getClass().getSimpleName())
                        .append(" - ").append(e.getMessage()).append("<br/>");
            }
        }
        return result.toString();
    }
}
