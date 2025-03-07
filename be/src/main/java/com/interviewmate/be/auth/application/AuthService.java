package com.interviewmate.be.auth.application;

import com.interviewmate.be.common.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    /**
     * methodName : logout
     * description : 로그아웃 처리 메서드
     *
     * @param request  HTTP 요청 객체
     * @param response HTTP 응답 객체
     */
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("로그아웃 : 쿠키 삭제");
        deleteCookie(response, "access_token");
        deleteCookie(response, "refresh_token");
    }

    /**
     * methodName : deleteCookie
     * description : 특정 쿠키를 삭제하는 메서드
     *
     * @param response HTTP 응답 객체
     * @param cookieName 삭제할 쿠키 이름
     */
    private void deleteCookie(HttpServletResponse response, String cookieName) {
        response.addHeader("Set-Cookie", cookieName + "=; Path=/; HttpOnly; Secure; Max-Age=0; SameSite=Lax");
    }
}
