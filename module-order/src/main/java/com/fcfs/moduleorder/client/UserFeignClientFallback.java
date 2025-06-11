package com.fcfs.moduleorder.client;

import com.fcfs.moduleorder.global.exception.CustomException;
import com.fcfs.moduleorder.global.exception.ErrorCode;
import com.fcfs.moduleorder.order.dto.response.UserEntityResponseDto;
import com.fcfs.moduleorder.order.dto.response.WishlistResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j(topic = "UserFeignClientFallback")
@Component
public class UserFeignClientFallback implements UserFeignClient {
    @Override
    public UserEntityResponseDto getUserEntity(Long userId) {
        log.warn("[Fallback] getUserEntity: userId={}, 서비스 장애!", userId);
        throw new CustomException(ErrorCode.FEIGN_ERROR);
    }

    @Override
    public WishlistResponseDto getWishlistEntity(Long userId) {
        log.warn("[Fallback] getWishlistEntity: userId={}, 서비스 장애!", userId);
        throw new CustomException(ErrorCode.FEIGN_ERROR);
    }

    @Override
    public void clearWishlist(Long userId) {
        // 장애 시 아무 동작도 하지 않음
    }
} 