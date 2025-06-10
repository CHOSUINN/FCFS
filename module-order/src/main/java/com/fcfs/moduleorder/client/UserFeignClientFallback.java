package com.fcfs.moduleorder.client;

import com.fcfs.moduleorder.order.dto.response.UserEntityResponseDto;
import com.fcfs.moduleorder.order.dto.response.WishlistResponseDto;
import com.fcfs.moduleorder.order.dto.WishlistItemDto;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class UserFeignClientFallback implements UserFeignClient {
    @Override
    public UserEntityResponseDto getUserEntity(Long userId) {
        return new UserEntityResponseDto(
                -1L,
                "[서비스 장애] 이메일 정보를 불러올 수 없습니다.",
                "[서비스 장애] 닉네임 정보를 불러올 수 없습니다.",
                "[서비스 장애] 주소 정보를 불러올 수 없습니다.",
                "[서비스 장애] 전화번호 정보를 불러올 수 없습니다.",
                "[서비스 장애] 역할 정보를 불러올 수 없습니다."
        );
    }

    @Override
    public WishlistResponseDto getWishlistEntity(Long userId) {
        return new WishlistResponseDto(
                -1L,
                userId,
                0,
                LocalDateTime.now(),
                List.of(new WishlistItemDto(-1L, "[서비스 장애] 상품명", 0, 0))
        );
    }

    @Override
    public void clearWishlist(Long userId) {
        // 장애 시 아무 동작도 하지 않음
    }
} 