package com.fcfs.fcfs.global.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    REGISTER_FAILURE(HttpStatus.BAD_REQUEST, 4001, "회원가입 실패");

    private final HttpStatus status;
    private final int code;
    private final String message;

    ErrorCode(HttpStatus status, int code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
