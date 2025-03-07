package com.interviewmate.be.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * packageName    : com.interviewmate.be.common.exception
 * fileName       : ErrorCode
 * author         : eumsoli
 * date           : 2025-03-07
 * description    : 프로젝트에서 사용되는 예외 코드와 메시지를 정의하는 Enum
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 인증 관련 예외
    INVALID_JWT_SIGNATURE(UNAUTHORIZED, "잘못된 JWT 서명입니다."),
    TOKEN_EXPIRED(UNAUTHORIZED, "만료된 JWT 토큰입니다."),
    UNSUPPORTED_TOKEN(UNAUTHORIZED, "지원되지 않는 JWT 토큰입니다."),
    INVALID_TOKEN(UNAUTHORIZED, "유효하지 않은 JWT 토큰입니다."),
    UNAUTHORIZED_ACCESS(UNAUTHORIZED, "인증되지 않은 접근입니다. 로그인 후 이용해 주세요.");

    private final HttpStatus httpStatus; // HTTP 상태 코드
    private final String message; // 에러 메시지
}
