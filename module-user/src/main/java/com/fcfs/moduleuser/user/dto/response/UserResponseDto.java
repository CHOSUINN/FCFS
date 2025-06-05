package com.fcfs.moduleuser.user.dto.response;

import com.fcfs.moduleuser.user.entity.User;

import java.time.LocalDateTime;

public record UserResponseDto(
        Long id,
        String email,
        String nickname,
        String address,
        String phoneNumber,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {
    public static UserResponseDto toDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getAddress(),
                user.getPhoneNumber(),
                user.getCreatedAt(),
                user.getModifiedAt()
        );
    }
}
