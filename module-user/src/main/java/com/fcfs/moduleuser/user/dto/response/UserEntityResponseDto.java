package com.fcfs.moduleuser.user.dto.response;

import com.fcfs.moduleuser.user.entity.User;

public record UserEntityResponseDto(
        Long userId,
        String email,
        String nickname,
        String address,
        String phoneNumber,
        String role
) {
    public static UserEntityResponseDto toDto(User user) {
        return new UserEntityResponseDto(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getAddress(),
                user.getPhoneNumber(),
                user.getRole().getAuthority()
        );
    }
}
