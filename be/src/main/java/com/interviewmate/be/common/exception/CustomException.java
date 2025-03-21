package com.interviewmate.be.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * packageName    : com.interviewmate.be.common.exception
 * fileName       : CustomException
 * author         : eumsoli
 * date           : 2025-03-07
 * description    : 프로젝트에서 사용되는 커스텀 예외 클래스
 */
@Getter
public class CustomException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String message;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.httpStatus = errorCode.getHttpStatus();
        this.message = errorCode.getMessage();
    }

}
