package com.fcfs.moduleproduct.global.scheduler;

import com.fcfs.moduleproduct.product.entity.Product;
import com.fcfs.moduleproduct.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j(topic = "Scheduler")
@Component
@RequiredArgsConstructor
public class Scheduler {

    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    // 상품 열리는 이벤트. 매주 토,일 오후 2시에 이벤트 열림. 이벤트는 항상 저녁 6시에 종료된다.
//    @Scheduled(cron = "0 0 14 * * Sat,Sun")
    @Scheduled(cron = "0 51 3 * * *")
    public void eventOpen() {
        List<Product> eventProducts = productRepository.findAllByIsEventTrue();

        for (Product product : eventProducts) {
            // 레디스에 재고 세팅
            String redisKey = "product:" + product.getId() + ":stock";
            Integer stock = product.getStock();

            if (Boolean.FALSE.equals(redisTemplate.hasKey(redisKey))) {
                redisTemplate.opsForValue().set(redisKey, String.valueOf(stock));
            }
        }
    }

    // 이벤트 종료.
    @Scheduled(cron = "0 0 18 * * Sat,Sun")
    public void eventClose() {
        List<Product> eventProducts = productRepository.findAllByIsEventTrue();

        for (Product product : eventProducts) {
            String redisKey = "product:" + product.getId() + ":stock";

            if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
                redisTemplate.delete(redisKey);
            }
        }
    }


    // 초, 분, 시, 일, 월, 주 순서
    // cron은 운영체제에서 특정 시간마다 어떠한 작업을 자동 수행하게 해주는 명령어가 cron이다. spring에서는 cron이라고 부르고 있다.
    // https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/scheduling/support/CronExpression.html
    // 해당 문서 참고
    @Scheduled(cron = "0 0 1  * * *") // 매일 새벽 1시
    public void updateOrder() {
        // 주문을 전부 불러온다.

        // 주문 상태를 확인.
        // 만약 주문 상태의 seq이 0, 1, 2이면 매일 새벽 1시에 + 1을 시켜서 주문상태가 변경되도록 함.
    }

}