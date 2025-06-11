package com.fcfs.moduleorder.client;

import com.fcfs.moduleorder.order.dto.response.UserEntityResponseDto;
import com.fcfs.moduleorder.order.dto.response.WishlistResponseDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service"
        , url = "${services.user.url}"
        , fallback = UserFeignClientFallback.class
)
@CircuitBreaker(name = "module-order-user-circuit-breaker")
@Retry(name = "module-order-user-retry")
public interface UserFeignClient {

    /** 사용자 정보 조회 */
    @GetMapping("/api/users/{userId}")
    UserEntityResponseDto getUserEntity(@PathVariable("userId") Long userId);

    /** 해당 유저의 위시리스트 조회 */
    @GetMapping("/api/wishlists/{userId}")
    WishlistResponseDto getWishlistEntity(@PathVariable("userId") Long userId);

    /** 해당 유저의 위시리스트 전체 삭제 */
    @DeleteMapping("/api/wishlists/remove/{userId}")
    void clearWishlist(@PathVariable("userId") Long userId);

}
