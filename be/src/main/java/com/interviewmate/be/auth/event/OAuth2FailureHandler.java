package com.interviewmate.be.auth.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewmate.be.common.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
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
        log.warn("OAuth2 로그인 실패: {}", exception.getMessage());

        ErrorCode errorCode = resolveOAuth2ErrorCode(exception.getMessage());

        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");

        String json = objectMapper.writeValueAsString(
                new ErrorResponse(errorCode.getHttpStatus().value(), errorCode.name(), errorCode.getMessage())
        );

        response.getWriter().write(json);
    }

    /**
     * methodName : resolveOAuth2ErrorCode
     * description : OAuth2 예외 메시지에 따라 ErrorCode 반환
     *
     * @param message 예외 메시지
     * @return ErrorCode 매핑된 에러 코드
     */
    private ErrorCode resolveOAuth2ErrorCode(String message) {
        String msg = message.toLowerCase();

        if (msg.contains("access_denied")) {
            return ErrorCode.OAUTH2_ACCESS_DENIED;
        } else if (msg.contains("redirect_uri_mismatch")) {
            return ErrorCode.OAUTH2_REDIRECT_URI_MISMATCH;
        } else if (msg.contains("invalid_scope")) {
            return ErrorCode.OAUTH2_INVALID_SCOPE;
        } else if (msg.contains("client_id")) {
            return ErrorCode.OAUTH2_CLIENT_ERROR;
        } else {
            return ErrorCode.OAUTH2_UNKNOWN_ERROR;
        }
    }

    /**
     * 내부 응답 포맷 클래스
     */
    private record ErrorResponse(int status, String error, String message) {}
}
