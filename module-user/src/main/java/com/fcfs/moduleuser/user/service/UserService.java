package com.fcfs.moduleuser.user.service;

import com.fcfs.moduleuser.user.dto.request.UserSignUpRequestDto;
import com.fcfs.moduleuser.user.dto.response.UserEntityResponseDto;
import com.fcfs.moduleuser.user.dto.response.UserResponseDto;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

public interface UserService {
    void registerUser(UserSignUpRequestDto requestDto) throws MessagingException, UnsupportedEncodingException;

    UserResponseDto verifyEmail(String token);

    UserResponseDto infoUser(Long userId);

    UserEntityResponseDto getUserEntity(Long userId);
}
