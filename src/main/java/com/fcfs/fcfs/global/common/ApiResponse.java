package com.fcfs.fcfs.global.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    private Boolean success;
    private Integer code;
    private String message;
    private T data;

    /**
     * 성공 응답을 만들 때 사용
     *
     * @param httpStatus    HTTP 상태코드(또는 비즈니스 코드)
     * @param message 요약 메시지
     * @param data    실제 payload
     * @param <T>     payload 타입
     * @return ApiResponse<T>
     */
    public static <T> ApiResponse<T> success(HttpStatus httpStatus, String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(httpStatus.value())
                .message(message)
                .data(data)
                .build();
    }

    // 간단한 성공 응답 생성 (데이터 없음)
    public static ApiResponse success(HttpStatus httpStatus, String message) {
        return success(httpStatus, message, null);
    }

}