package com.interviewmate.be.auth.event;

import com.interviewmate.be.auth.domain.OAuth2UserInfo;
import com.interviewmate.be.auth.domain.OAuth2UserInfoFactory;
import com.interviewmate.be.auth.domain.OAuth2UserPrincipal;
import com.interviewmate.be.common.security.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
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
        OAuth2UserPrincipal oAuth2User = (OAuth2UserPrincipal) authentication.getPrincipal();
        String provider = oAuth2User.getProvider();

        log.info("OAuth2AuthenticationSuccessHandler: provider={}", provider);

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

        // Secure HttpOnly Cookie에 JWT 저장 (클라이언트에서 접근 불가능)
        response.addHeader("Set-Cookie", "access_token=" + accessToken + "; Path=/; HttpOnly; Secure; SameSite=Lax");
        response.addHeader("Set-Cookie", "refresh_token=" + refreshToken + "; Path=/; HttpOnly; Secure; SameSite=Lax");

        // TODO 프론트엔드 페이지로 변경하기
        response.sendRedirect("http://localhost:3000/oauth2/success");
    }

}
