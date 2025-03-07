package com.interviewmate.be.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * packageName    : com.interviewmate.be.common.security
 * fileName       : JwtAuthenticationFilter
 * author         : eumsoli
 * date           : 2025-03-07
 * description    : JWT 인증 필터 - 요청에서 JWT를 추출하여 검증하고 SecurityContext에 저장.
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * methodName : doFilterInternal
     * description : HTTP 요청에서 JWT를 추출하고 검증하여 SecurityContext에 저장.
     *
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param chain 필터 체인
     * @throws ServletException 필터 처리 중 발생할 수 있는 예외
     * @throws IOException 입출력 예외
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String token = resolveToken(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            Map<String, Object> claims = jwtTokenProvider.getClaims(token);

            // 소셜 로그인 제공자별로 사용자 ID 가져오기
            String username = (String) (claims.get("sub") != null ? claims.get("sub") : claims.get("id"));

            // JWT에서 사용자 정보를 기반으로 SecurityContext에 저장
            UserDetails userDetails = new User(username, "", Collections.emptyList());
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }

    /**
     * methodName : resolveToken
     * description : HTTP 요청 헤더에서 JWT를 추출.
     *
     * @param request HTTP 요청 객체
     * @return JWT 토큰 문자열 또는 null
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
