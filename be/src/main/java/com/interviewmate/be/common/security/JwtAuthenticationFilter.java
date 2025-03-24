package com.interviewmate.be.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewmate.be.auth.domain.User;
import com.interviewmate.be.common.exception.CustomException;
import com.interviewmate.be.common.exception.ErrorCode;
import com.interviewmate.be.infrastructure.persistence.auth.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
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
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 응답용

    /**
     * methodName : doFilterInternal
     * description : HTTP 요청에서 JWT를 추출하고 검증하여 SecurityContext에 저장.
     *
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param filterChain 필터 체인
     * @throws ServletException 필터 처리 중 발생할 수 있는 예외
     * @throws IOException 입출력 예외
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = resolveToken(request);

        try {
            if (token != null && jwtTokenProvider.validateToken(token)) {
                Map<String, Object> claims = jwtTokenProvider.getClaims(token);
                String providerId = (String) claims.get("providerId");

                // User 엔티티 조회
                User user = userRepository.findByProviderId(providerId)
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);

        } catch (CustomException ex) {
            handleJwtException(response, ex);
        }
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

    /**
     * methodName : handleJwtException
     * description : JWT 검증 실패 시 JSON 형식으로 예외 응답 전송
     *
     * @param response HTTP 응답 객체
     * @param ex CustomException 객체
     * @throws IOException JSON 응답 작성 중 예외 발생 시
     */
    private void handleJwtException(HttpServletResponse response, CustomException ex) throws IOException {
        log.warn("JWT 인증 실패: {}", ex.getMessage());

        response.setStatus(ex.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errorBody = Map.of(
                "status", ex.getHttpStatus().value(),
                "message", ex.getMessage(),
                "error", ex.getHttpStatus().getReasonPhrase()
        );

        response.getWriter().write(objectMapper.writeValueAsString(errorBody));
    }

}
