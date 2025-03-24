package com.interviewmate.be.auth.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewmate.be.common.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * packageName    : com.interviewmate.be.auth.event
 * fileName       : OAuth2FailureHandler
 * author         : eumsoli
 * date           : 2025-03-08
 * description    : OAuth2 로그인 실패 시 처리하는 핸들러
 */
@Slf4j
@Component
public class OAuth2FailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * methodName : onAuthenticationFailure
     * description : OAuth2 로그인 실패 시 JSON 응답 반환
     *
     * @param request     HTTP 요청
     * @param response    HTTP 응답
     * @param exception   인증 실패 예외 객체
     * @throws IOException 예외 발생 시 처리
     * @throws ServletException 예외 발생 시 처리
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        log.error("OAuth2 로그인 실패: {}", exception.getMessage());

        ErrorCode errorCode = resolveOAuth2ErrorCode(exception);

        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");

        String json = objectMapper.writeValueAsString(
                new ErrorResponse(errorCode.getHttpStatus().value(), errorCode.name(), errorCode.getMessage())
        );

        response.getWriter().write(json);
    }

    /**
     * methodName : resolveOAuth2ErrorCode
     * description : OAuth2 로그인 실패 시 발생한 예외 객체를 분석하여 대응하는 ErrorCode를 반환하는 메서드
     *
     * @param exception OAuth2 인증 실패 시 발생한 AuthenticationException 객체
     * @return ErrorCode 매핑된 에러 코드 (OAuth2 관련 코드 또는 알 수 없는 오류 코드)
     */
    private ErrorCode resolveOAuth2ErrorCode(AuthenticationException exception) {
        if (exception instanceof OAuth2AuthenticationException authEx) {
            String errorCode = authEx.getError().getErrorCode();

            return switch (errorCode) {
                case "access_denied" -> ErrorCode.OAUTH2_ACCESS_DENIED;
                case "redirect_uri_mismatch" -> ErrorCode.OAUTH2_REDIRECT_URI_MISMATCH;
                case "invalid_scope" -> ErrorCode.OAUTH2_INVALID_SCOPE;
                case "client_id" -> ErrorCode.OAUTH2_CLIENT_ERROR;
                default -> ErrorCode.UNKNOWN_ERROR;
            };
        }

        return ErrorCode.UNKNOWN_ERROR;
    }

    /**
     * 내부 응답 포맷 클래스
     */
    private record ErrorResponse(int status, String error, String message) {}
}
