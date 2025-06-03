package com.fcfs.fcfs.user.service;

import com.fcfs.fcfs.user.dto.request.UserSignUpRequestDto;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

public interface UserService {
    void registerUser(UserSignUpRequestDto requestDto) throws MessagingException, UnsupportedEncodingException;

    void verifyEmail(String token);
}
