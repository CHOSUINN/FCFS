package com.fcfs.moduleproduct.global.exception;

public class CustomException extends RuntimeException {

    private final ErrorInterface errorCode;

    /** ErrorCode에서 정의한 getDetail()이 super(message)로 들어갑니다. */
    public CustomException(ErrorInterface errorCode) {
        super(errorCode.getDetail());
        this.errorCode = errorCode;
    }

    /** 커스텀 detail을 덮어쓰고 싶을 때 사용 */
    public CustomException(ErrorInterface errorCode, String customDetail) {
        super(customDetail);
        this.errorCode = errorCode;
    }

    /** 체이닝용 */
    public CustomException(ErrorInterface errorCode, Throwable cause) {
        super(errorCode.getDetail(), cause);
        this.errorCode = errorCode;
    }

    /** 커스텀 메시지 + 체이닝 */
    public CustomException(ErrorInterface errorCode, String customDetail, Throwable cause) {
        super(customDetail, cause);
        this.errorCode = errorCode;
    }

    public ErrorInterface getErrorCode() {
        return errorCode;
    }

    /** convenience 메서드 */
    public Integer getStatus() {
        return errorCode.getHttpStatus();
    }

    public String getType() {
        return errorCode.getErrorCode();
    }

    public String getTitle() {
        return errorCode.getTitle();
    }

    @Override
    public String getMessage() {
        // super.getMessage()는 생성자에서 받은 detail(혹은 customDetail)
        return super.getMessage();
    }
}
