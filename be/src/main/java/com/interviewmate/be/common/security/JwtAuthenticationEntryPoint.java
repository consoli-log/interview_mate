package com.interviewmate.be.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewmate.be.common.exception.ErrorCode;
import com.interviewmate.be.common.util.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * packageName    : com.interviewmate.be.common.security
 * fileName       : JwtAuthenticationEntryPoint
 * author         : eumsoli
 * date           : 2025-03-23
 * description    : 인증되지 않은 사용자의 요청에 대해 401 Unauthorized 에러를 반환하는 EntryPoint
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * methodName : commence
     * description : 인증되지 않은 사용자가 접근 시 401 반환
     *
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param authException 인증 예외
     * @throws IOException 입출력 예외
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        ErrorResponse.sendErrorResponse(response, ErrorCode.UNAUTHORIZED_ACCESS);

    }

}
