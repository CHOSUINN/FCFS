package com.fcfs.fcfs.global.security.dto;

public record LoginRequestDto(
        String email,
        String password
) {}