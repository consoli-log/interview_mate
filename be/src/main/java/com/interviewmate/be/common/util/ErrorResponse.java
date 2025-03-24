package com.interviewmate.be.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewmate.be.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.Map;

/**
 * packageName    : com.interviewmate.be.common.util
 * fileName       : ErrorResponse
 * author         : eumsoli
 * date           : 2025-03-24
 * description    : 공통 에러 응답 JSON 포맷 처리 유틸리티 클래스
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * methodName : sendErrorResponse
     * description : 에러 응답을 JSON 포맷으로 반환
     *
     * @param response  HttpServletResponse 객체
     * @param errorCode ErrorCode enum
     * @throws IOException JSON 변환 예외
     */
    public static void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errorResponse = Map.of(
                "status", errorCode.getHttpStatus().value(),
                "error", errorCode.name(),
                "message", errorCode.getMessage()
        );

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

}