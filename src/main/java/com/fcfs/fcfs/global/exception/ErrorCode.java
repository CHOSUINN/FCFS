package com.fcfs.fcfs.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode implements ErrorInterface {

    USER_NOT_FOUND(
            HttpStatus.NOT_FOUND.value(),
            "USER_NOT_FOUND",
            "User Not Found",
            "요청하신 사용자를 찾을 수 없습니다. 이메일을 다시 확인해주세요."
    ),
    INVALID_PARAMETER(
            HttpStatus.BAD_REQUEST.value(),
            "INVALID_PARAMETER",
            "Invalid Parameter",
            "필수 파라미터가 유효하지 않습니다."
    ),
    INTERNAL_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "INTERNAL_ERROR",
            "Internal Server Error",
            "서버 내부 오류가 발생했습니다."
    );

    private final Integer httpStatus;
    private final String errorCode;
    private final String title;
    private final String detail;

    ErrorCode(Integer httpStatus, String errorCode, String title, String detail) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.title = title;
        this.detail = detail;
    }

    @Override
    public Integer getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDetail() {
        return detail;
    }
}
