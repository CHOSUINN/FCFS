package com.fcfs.fcfs.user.service;

import com.fcfs.fcfs.user.dto.request.UserSignUpRequestDto;
import com.fcfs.fcfs.user.dto.response.UserResponseDto;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

public interface UserService {
    void registerUser(UserSignUpRequestDto requestDto) throws MessagingException, UnsupportedEncodingException;

    UserResponseDto verifyEmail(String token);

    UserResponseDto infoUser(Long userId);
}
