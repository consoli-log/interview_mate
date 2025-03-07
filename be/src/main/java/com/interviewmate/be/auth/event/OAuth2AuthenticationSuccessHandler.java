package com.interviewmate.be.auth.event;

import com.interviewmate.be.auth.domain.OAuth2UserInfo;
import com.interviewmate.be.auth.domain.OAuth2UserInfoFactory;
import com.interviewmate.be.common.security.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * packageName    : com.interviewmate.be.auth.event
 * fileName       : OAuth2AuthenticationSuccessHandler
 * author         : eumsoli
 * date           : 2025-03-07
 * description    : OAuth2 로그인 성공 후 JWT를 발급하고 응답하는 핸들러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * methodName : onAuthenticationSuccess
     * description : OAuth2 로그인 성공 시 JWT 발급 후 클라이언트에 응답
     *
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param authentication 인증 객체 (로그인한 사용자 정보 포함)
     * @throws IOException 입출력 예외
     * @throws ServletException 서블릿 예외
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String provider = request.getParameter("provider"); // OAuth2 제공자 정보

        // 제공자별 사용자 정보 객체 생성
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, oAuth2User.getAttributes());

        log.info("OAuth2 로그인 성공: provider={}, userId={}, email={}", provider, userInfo.getId(), userInfo.getEmail());

        // JWT 생성
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userInfo.getId());
        claims.put("email", userInfo.getEmail());
        claims.put("provider", provider);

        String accessToken = jwtTokenProvider.generateAccessToken(claims);
        String refreshToken = jwtTokenProvider.generateRefreshToken(claims);

        // 클라이언트에 JWT 반환 (헤더 설정)
        response.setHeader("Authorization", "Bearer " + accessToken);
        response.setHeader("Refresh-Token", refreshToken);
        response.setStatus(HttpServletResponse.SC_OK);
    }

}
