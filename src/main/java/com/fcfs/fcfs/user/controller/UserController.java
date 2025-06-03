package com.fcfs.fcfs.user.controller;

import com.fcfs.fcfs.global.common.ApiResponse;
import com.fcfs.fcfs.global.security.UserDetailsImpl;
import com.fcfs.fcfs.user.dto.request.UserSignUpRequestDto;
import com.fcfs.fcfs.user.dto.response.UserResponseDto;
import com.fcfs.fcfs.user.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@Slf4j(topic = "UserController")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @PostMapping("/auth/register")
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody @Valid UserSignUpRequestDto requestDto) throws MessagingException, UnsupportedEncodingException {
        log.info("register user: {}", requestDto.email());
        userService.registerUser(requestDto);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.CREATED,
                "이메일 인증절차로 넘어가세요."));
    }

    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<UserResponseDto>> verify(@RequestParam(name = "token") String token) {
        log.info("verify token: {}", token);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "회원가입에 성공하였습니다.",
                userService.verifyEmail(token)
        ));
    }

    @GetMapping("/users/me")
    public ResponseEntity<ApiResponse<UserResponseDto>> info(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "회원정보 조회에 성공하였습니다.",
                userService.infoUser(userDetails.getUser().getId())
        ));
    }
}
