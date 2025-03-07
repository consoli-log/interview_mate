package com.interviewmate.be.common.exception;

import lombok.Getter;

/**
 * packageName    : com.interviewmate.be.common.exception
 * fileName       : CustomException
 * author         : eumsoli
 * date           : 2025-03-07
 * description    : 프로젝트에서 사용되는 커스텀 예외 클래스
 */
@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage()); // 기본 메세지
        this.errorCode = errorCode;
    }

    public CustomException(ErrorCode errorCode, String message) {
        super(message);  // 커스텀 메세지
        this.errorCode = errorCode;
    }

}
