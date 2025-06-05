package com.fcfs.moduleproduct.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


@Slf4j(topic = "GlobalExceptionHandler")
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * CustomException 핸들러: RFC 7807 Problem Detail 형식으로 반환
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ProblemDetail> handleCustomException(CustomException ex, HttpServletRequest request) {
        // ErrorInterface를 통해 필요한 정보 꺼내기
        ErrorInterface ec = ex.getErrorCode();

        // 1) status와 detail(=예외 메시지)를 세팅
        // ProblemDetail은 이미 구현되어 있는 객체이다. 가져다 쓰자.
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.valueOf(ec.getHttpStatus()),
                ex.getMessage()   // detail 필드에 표시될 메시지를 꺼냅니다.
        );

        // 2) type 필드 설정 (RFC 7807의 “type” → ec.getErrorCode())
        //    예: "https://api.myapp.com/errors/USER_NOT_FOUND"
        problem.setType(URI.create("https://localhost:8081/errors/" + ec.getErrorCode()));

        // 3) title 필드 설정 (짧은 요약)
        problem.setTitle(ec.getTitle());

        // 4) instance 필드 설정 (문제가 발생한 리소스 경로)
        problem.setInstance(URI.create(request.getRequestURI()));

        // 5) 필요하다면 추가 헤더 세팅 (여기서는 기본 헤더만 전달)
        HttpHeaders headers = new HttpHeaders();
        // Content-Type: application/problem+json 은 자동으로 붙습니다.

        return new ResponseEntity<>(problem, headers, HttpStatus.valueOf(ec.getHttpStatus()));
    }

    /**
     * 입력값 유효성 검사 예외 처리:
     * MethodArgumentNotValidException 발생 시, 필드별 에러 메시지를 모아서 ProblemDetail로 반환
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        BindingResult bindingResult = ex.getBindingResult();
        Map<String, String> invalidFields = new HashMap<>();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            invalidFields.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        log.error("Validation error: {}", invalidFields);

        // 사용할 에러 코드와 메시지를 ErrorCode enum에서 가져옵니다.
        ErrorCode ec = ErrorCode.INVALID_PARAMETER;

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.valueOf(ec.getHttpStatus()),
                "입력값 검증에 실패했습니다."
        );

        problem.setType(URI.create("https://localhost:8081/errors/" + ec.getErrorCode()));
        problem.setTitle(ec.getTitle());
        problem.setInstance(URI.create(request.getRequestURI()));

        // RFC 7807 표준에 없는 필드를 확장 속성으로 추가합니다.
        problem.setProperty("invalidFields", invalidFields);

        return new ResponseEntity<>(problem, HttpStatus.valueOf(ec.getHttpStatus()));
    }

    /**
     * 메서드 인자 타입 불일치 예외 처리:
     * MethodArgumentTypeMismatchException 발생 시 ProblemDetail로 반환
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        log.error("Type mismatch: {}", ex.getMessage(), ex);

        // 사용할 에러 코드와 메시지를 ErrorCode enum에서 가져옵니다.
        ErrorCode ec = ErrorCode.INTERNAL_ERROR;

        String detailMessage = String.format(
                "잘못된 타입의 값이 입력되었습니다. '%s' 필드에는 '%s' 타입이 필요합니다. 요청한 값: %s",
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "알 수 없음",
                ex.getValue()
        );

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.valueOf(ec.getHttpStatus()),
                detailMessage
        );

        problem.setType(URI.create("https://localhost:8081/errors/" + ec.getErrorCode()));
        problem.setTitle(ec.getTitle());
        problem.setInstance(URI.create(request.getRequestURI()));

        return new ResponseEntity<>(problem, HttpStatus.valueOf(ec.getHttpStatus()));
    }

    /**
     * 데이터베이스 예외 처리:
     * SQLException 발생 시 ProblemDetail로 반환
     */
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ProblemDetail> handleSQLException(SQLException ex, HttpServletRequest request) {
        log.error("Database error occurred: {}", ex.getMessage(), ex);

        // 사용할 에러 코드와 메시지를 ErrorCode enum에서 가져옵니다.
        ErrorCode ec = ErrorCode.INTERNAL_ERROR;

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "데이터베이스 처리 중 오류가 발생했습니다."
        );

        problem.setType(URI.create("https://localhost:8081/errors/" + ec.getErrorCode()));
        problem.setTitle(ec.getTitle());
        problem.setInstance(URI.create(request.getRequestURI()));

        return new ResponseEntity<>(problem, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 그 외 모든 예외 처리:
     * 예상치 못한 예외(Exception) 발생 시 ProblemDetail로 반환
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleAllExceptions(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);

        ErrorCode ec = ErrorCode.INTERNAL_ERROR;

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "서버 내부 오류가 발생했습니다."
        );

        problem.setType(URI.create("https://localhost:8081/errors/" + ec.getErrorCode()));
        problem.setTitle(ec.getTitle());
        problem.setInstance(URI.create(request.getRequestURI()));

        return new ResponseEntity<>(problem, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}