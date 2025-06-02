package com.fcfs.fcfs.user.controller;

import com.fcfs.fcfs.global.common.ApiResponse;
import com.fcfs.fcfs.user.entity.User;
import com.fcfs.fcfs.user.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserServiceImpl userService;

    @PostMapping("/auth/register")
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody User user) {
        log.info("register user: {}", user);
        userService.registerUser(user);
        return ResponseEntity.ok(ApiResponse.success("회원가입 성공"));
    }



}
