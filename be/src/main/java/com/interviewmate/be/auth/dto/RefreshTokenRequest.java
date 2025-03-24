package com.interviewmate.be.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * packageName    : com.interviewmate.be.auth.dto
 * fileName       : RefreshTokenRequest
 * author         : eumsoli
 * date           : 2025-03-21
 * description    : Refresh Token을 이용한 Access Token 재발급 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequest {

    @Schema(description = "사용자의 Refresh Token", example = "eyJhbGciOiJIUzI1...")
    private String refreshToken;
}
