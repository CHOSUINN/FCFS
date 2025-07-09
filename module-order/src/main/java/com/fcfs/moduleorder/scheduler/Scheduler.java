//package com.fcfs.moduleorder.scheduler;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//@Slf4j(topic = "Scheduler")
//@Component
//@RequiredArgsConstructor
//public class Scheduler {
//
//    // 상품 열리는 이벤트. 매주 토,일 오후 2시에 이벤트 열림
//    @Scheduled(cron = "0 0 14 * * Sat,Sun")
//    public void eventOpen() {
//        // 이벤트에 해당하는 주문 목록 불러옴.
//
//        // 해당 상품들을 위시리스트에 담을 수 있게 함.
//    }
//
//    // 초, 분, 시, 일, 월, 주 순서
//    // cron은 운영체제에서 특정 시간마다 어떠한 작업을 자동 수행하게 해주는 명령어가 cron이다. spring에서는 cron이라고 부르고 있다.
//    // https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/scheduling/support/CronExpression.html
//    // 해당 문서 참고
//    @Scheduled(cron = "0 0 1  * * *") // 매일 새벽 1시
//    public void updateOrder() {
//        // 주문을 전부 불러온다.
//
//        // 주문 상태를 확인.
//        // 만약 주문 상태의 seq이 0, 1, 2이면 매일 새벽 1시에 + 1을 시켜서 주문상태가 변경되도록 함.
//    }
//
//}