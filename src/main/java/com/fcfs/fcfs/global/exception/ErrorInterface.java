package com.fcfs.fcfs.global.exception;

public interface ErrorInterface {
    /** HTTP 상태 코드 (예: 404) */
    Integer getHttpStatus();

    /** “type” 필드로 사용할 URI 구분자 (예: "USER_NOT_FOUND") */
    String getErrorCode();

    /** “title” 필드로 사용할 짧은 제목 (예: "User Not Found") */
    String getTitle();

    /** “detail” 필드(상세 메시지) */
    String getDetail();
}
