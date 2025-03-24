package com.interviewmate.be.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * packageName    : com.interviewmate.be.common.exception
 * fileName       : GlobalExceptionHandler
 * author         : eumsoli
 * date           : 2025-03-08
 * description    : 전역 예외를 처리하는 핸들러
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * CustomException 처리
     *
     * @param ex 발생한 CustomException 객체
     * @return HTTP 상태 코드와 예외 메시지를 포함한 응답 반환
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, Object>> handleCustomException(CustomException ex) {
        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(Map.of(
                        "status", ex.getHttpStatus().value(),
                        "error", ex.getHttpStatus().getReasonPhrase(),
                        "message", ex.getMessage()
                ));
    }

}
