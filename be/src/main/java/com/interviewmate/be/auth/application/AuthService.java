package com.interviewmate.be.auth.application;

import com.interviewmate.be.common.exception.CustomException;
import com.interviewmate.be.common.exception.ErrorCode;
import com.interviewmate.be.common.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;

/**
 * packageName    : com.interviewmate.be.auth.application
 * fileName       : AuthService
 * author         : eumsoli
 * date           : 2025-03-08
 * description    : OAuth2 로그인 관련 인증 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;

    /**
     * methodName : refreshAccessToken
     * description : Refresh Token을 검증하고 새로운 Access Token을 발급
     *
     * @param refreshToken 클라이언트가 보낸 Refresh Token
     * @return 새로운 Access Token
     */
    @Transactional
    public String refreshAccessToken(String refreshToken) {
        // Refresh Token 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        // Refresh Token에서 Claims 추출
        Map<String, Object> claims = jwtTokenProvider.getClaims(refreshToken);
        String providerId = (String) claims.get("providerId");

        log.info("RefreshToken 재발급 요청: providerId={}", providerId);

        // Redis에서 저장된 Refresh Token 확인
        String storedRefreshToken = tokenService.getRefreshToken(providerId);
        if (storedRefreshToken == null || !Objects.equals(storedRefreshToken, refreshToken)) {
            throw new CustomException(ErrorCode.TOKEN_NOT_FOUND);
        }

        // 새로운 Access Token 발급
        return jwtTokenProvider.generateAccessToken(claims);
    }

    /**
     * methodName : logout
     * description : 로그아웃 시 Refresh Token 삭제
     *
     * @param providerId 로그아웃할 사용자의 providerId
     */
    @Transactional
    public void logout(String providerId) {
        log.info("RefreshToken 로그아웃 요청: providerId={}", providerId);
        tokenService.deleteRefreshToken(providerId);
    }

}
