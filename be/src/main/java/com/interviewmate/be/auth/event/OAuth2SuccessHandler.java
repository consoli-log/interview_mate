package com.interviewmate.be.auth.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewmate.be.auth.application.TokenService;
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
 * fileName       : OAuth2SuccessHandler
 * author         : eumsoli
 * date           : 2025-03-07
 * description    : OAuth2 로그인 성공 후 회원 여부를 체크하고 JWT를 발급하는 핸들러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * methodName : onAuthenticationSuccess
     * description : OAuth2 로그인 성공 시 JWT 발급
     *
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param authentication 인증 객체 (로그인한 사용자 정보 포함)
     * @throws IOException 입출력 예외
     * @throws ServletException 서블릿 예외
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2UserPrincipal principal = (OAuth2UserPrincipal) authentication.getPrincipal();
        String provider = principal.getProvider();

        log.info("OAuth2SuccessHandler: provider={}", provider);

        // 제공자별 사용자 정보 객체 생성
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, principal.getAttributes());

        log.info("OAuth2 로그인 성공: provider={}, providerId={}, email={}, name={}", provider, userInfo.getId(), userInfo.getEmail(), userInfo.getName());

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", userInfo.getEmail());
        claims.put("name", userInfo.getName());
        claims.put("providerId", userInfo.getId());
        claims.put("provider", provider);

        // JWT 생성
        String accessToken = jwtTokenProvider.generateAccessToken(claims);
        String refreshToken = jwtTokenProvider.generateRefreshToken(claims);

        // Refresh Token을 Redis에 저장
        tokenService.saveRefreshToken(userInfo.getId(), refreshToken);

        Map<String, String> tokenResponse = new HashMap<>();
        tokenResponse.put("accessToken", accessToken);
        tokenResponse.put("refreshToken", refreshToken);

        // JSON 형태로 응답
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(tokenResponse));
    }

}
