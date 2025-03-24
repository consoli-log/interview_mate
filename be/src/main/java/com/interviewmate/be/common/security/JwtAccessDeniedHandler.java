package com.interviewmate.be.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewmate.be.common.exception.ErrorCode;
import com.interviewmate.be.common.util.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * packageName    : com.interviewmate.be.common.security
 * fileName       : JwtAccessDeniedHandler
 * author         : eumsoli
 * date           : 2025-03-24
 * description    : 인증은 되었지만 권한이 없는 사용자에게 403 Forbidden JSON 응답을 반환하는 핸들러
 */
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * methodName : handle
     * description : 인증된 사용자가 권한 없는 리소스 접근 시 403 반환
     *
     * @param request  HttpServletRequest 요청
     * @param response HttpServletResponse 응답
     * @param accessDeniedException 발생한 권한 예외
     * @throws IOException 입출력 예외
     */
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        ErrorResponse.sendErrorResponse(response, ErrorCode.FORBIDDEN_ACCESS);

    }
}
