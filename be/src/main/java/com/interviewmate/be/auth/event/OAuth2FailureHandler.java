package com.interviewmate.be.auth.event;

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

    /**
     * methodName : onAuthenticationFailure
     * description : OAuth2 로그인 실패 시 실행되는 메서드
     *
     * @param request   HTTP 요청 객체
     * @param response  HTTP 응답 객체
     * @param exception 인증 실패 예외
     * @throws IOException 입출력 예외
     * @throws ServletException 서블릿 예외
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.error("OAuth2 로그인 실패: {}", exception.getMessage());

        response.sendRedirect("/login?error=true"); // 로그인 실패 시 리다이렉트
    }
}
