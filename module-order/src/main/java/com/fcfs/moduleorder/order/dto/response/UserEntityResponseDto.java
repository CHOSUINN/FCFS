package com.fcfs.moduleorder.order.dto.response;

public record UserEntityResponseDto(
        Long userId,
        String email,
        String nickname,
        String address,
        String phoneNumber,
        String role
) {
}
