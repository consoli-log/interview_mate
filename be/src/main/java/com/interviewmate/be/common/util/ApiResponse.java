package com.interviewmate.be.common.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * packageName    : com.interviewmate.be.common.response
 * fileName       : ApiResponse
 * author         : eumsoli
 * date           : 2025-03-08
 * description    : 공통 API 응답 객체
 */
@Getter
@RequiredArgsConstructor
public class ApiResponse<T> {
    private final boolean success;
    private final T data;

    /**
     * 성공 응답 생성
     * @param data 응답 데이터
     * @return ApiResponse<T>
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data);
    }

    /**
     * 성공 응답 (데이터 없음)
     * @return ApiResponse<Void>
     */
    public static ApiResponse<Void> success() {
        return new ApiResponse<>(true, null);
    }
}
