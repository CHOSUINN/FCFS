package com.fcfs.fcfs.user.controller;

import com.fcfs.fcfs.global.common.ApiResponse;
import com.fcfs.fcfs.user.dto.UserSignUpRequestDto;
import com.fcfs.fcfs.user.dto.request.UserLoginRequestDto;
import com.fcfs.fcfs.user.service.UserServiceImpl;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@Slf4j(topic = "UserController")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserServiceImpl userService;

    @PostMapping("/auth/register")
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody @Valid UserSignUpRequestDto requestDto) throws MessagingException, UnsupportedEncodingException {
        log.info("register user: {}", requestDto.email());
        userService.registerUser(requestDto);
        return ResponseEntity.ok(ApiResponse.success("이메일 인증절차로 넘어가세요."));
    }

    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verify(@RequestParam(name = "token") String token) {
        log.info("verify token: {}", token);
        userService.verifyEmail(token);
        return ResponseEntity.ok(ApiResponse.success("회원가입에 성공하였습니다."));
    }
}
