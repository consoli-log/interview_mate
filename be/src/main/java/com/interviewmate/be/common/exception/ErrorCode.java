package com.interviewmate.be.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

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

    // 200 OK
    // 201 Created
    // 204 No Content

    // 400 Bad Request
    // 401 Unauthorized
    // 403 Forbidden
    // 404 Not Found
    // 409 Conflict
    // 500 Internal Server Error

    // 토큰 관련 예외
    INVALID_JWT_SIGNATURE(UNAUTHORIZED, "잘못된 JWT 서명입니다."),
    TOKEN_EXPIRED(UNAUTHORIZED, "만료된 JWT 토큰입니다."),
    UNSUPPORTED_TOKEN(UNAUTHORIZED, "지원되지 않는 JWT 토큰입니다."),
    INVALID_TOKEN(UNAUTHORIZED, "유효하지 않은 JWT 토큰입니다."),
    UNAUTHORIZED_ACCESS(UNAUTHORIZED, "인증되지 않은 접근입니다. 로그인 후 이용해 주세요."),
    TOKEN_NOT_FOUND(UNAUTHORIZED, "Refresh Token을 찾을 수 없습니다."),

    // 인증 실패 관련 예외
    OAUTH2_ACCESS_DENIED(UNAUTHORIZED, "사용자가 로그인 인증을 취소했습니다."),
    OAUTH2_REDIRECT_URI_MISMATCH(BAD_REQUEST, "리디렉션 URI가 잘못되었습니다."),
    OAUTH2_INVALID_SCOPE(BAD_REQUEST, "요청한 권한이 거부되었습니다."),
    OAUTH2_CLIENT_ERROR(BAD_REQUEST, "OAuth2 클라이언트 설정이 잘못되었습니다."),
    OAUTH2_UNKNOWN_ERROR(INTERNAL_SERVER_ERROR, "알 수 없는 로그인 오류가 발생했습니다."),

    // 사용자 관련 예외
    EMAIL_ALREADY_EXISTS(CONFLICT, "이미 존재하는 이메일입니다.");

    private final HttpStatus httpStatus; // HTTP 상태 코드
    private final String message; // 에러 메시지
}
