package com.fcfs.moduleproduct.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode implements ErrorInterface {

    // 유저
    USER_NOT_FOUND(
            HttpStatus.NOT_FOUND.value(),
            "USER_NOT_FOUND",
            "User Not Found",
            "요청하신 사용자를 찾을 수 없습니다."
    ),

    // 상품
    PRODUCT_NOT_FOUND(
            HttpStatus.NOT_FOUND.value(),
            "PRODUCT_NOT_FOUND",
            "Product Not Found",
            "상품을 찾을 수 없습니다."
    ),

    // 위시리스트
    WISHLIST_NOT_FOUND(
            HttpStatus.NOT_FOUND.value(),
            "WISHLIST_NOT_FOUND",
            "Wishlist Not Found",
            "위시리스트가 존재하지 않습니다."
    ),
    WISHLIST_DETAIL_NOT_FOUND(
            HttpStatus.NOT_FOUND.value(),
            "WISHLIST_DETAIL_NOT_FOUND",
            "Wishlist Detail Not Found",
            "위시리스트 안에 해당 상품이 존재하지 않습니다."
    ),

    // 주문
    ORDER_FAILURE_EMPTY_WISHLIST(
            HttpStatus.NOT_FOUND.value(),
            "EMPTY_WISHLIST",
            "Empty Wishlist",
            "위시리스트가 비어 있습니다."
    ),
    ORDER_NOT_FOUND(
            HttpStatus.NOT_FOUND.value(),
            "ORDER_NOT_FOUND",
            "Order Not Found",
            "해당 주문은 존재하지 않습니다"
    ),
    ORDER_FORBIDDEN(
            HttpStatus.FORBIDDEN.value(),
            "ORDER_FORBIDDEN",
            "Order Forbidden",
            "주문에 대한 접근 권한이 없습니다."
    ),
    ORDER_CANCELLATION_FAILURE(
            HttpStatus.FORBIDDEN.value(),
            "ORDER_CANCELLATION_FAILURE",
            "Order Cancellation Failure",
            "주문을 취소할 수 없는 상태입니다."
    ),
    ORDER_RETURN_NOT_ALLOWED(
            HttpStatus.BAD_REQUEST.value(),
            "ORDER_RETURN_NOT_ALLOWED",
            "Order Return Not Allowed",
            "반품이 불가능한 상태입니다."
    ),
    ORDER_RETURN_EXPIRED(
            HttpStatus.BAD_REQUEST.value(),
            "ORDER_RETURN_EXPIRED",
            "Order Return Expired",
            "반송 가능 기간을 초과하였습니다."
    ),


    INVALID_PARAMETER(
            HttpStatus.BAD_REQUEST.value(),
            "INVALID_PARAMETER",
            "Invalid Parameter",
            "필수 파라미터가 유효하지 않습니다."
    ),

    // 서버에러
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
