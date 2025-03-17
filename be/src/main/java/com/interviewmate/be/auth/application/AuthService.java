package com.interviewmate.be.auth.application;

import com.interviewmate.be.auth.domain.User;
import com.interviewmate.be.auth.dto.SignupRequest;
import com.interviewmate.be.common.exception.CustomException;
import com.interviewmate.be.common.exception.ErrorCode;
import com.interviewmate.be.common.security.JwtTokenProvider;
import com.interviewmate.be.infrastructure.persistence.auth.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

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

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * methodName : isNewUser
     * description : 이메일을 기반으로 기존 회원 여부 확인
     *
     * @param email 사용자 이메일
     * @return boolean 신규 사용자 여부 (true: 신규, false: 기존 회원)
     */
    public boolean isNewUser(String email) {
        return !userRepository.existsByEmail(email);
    }

    /**
     * methodName : signup
     * description : 신규 사용자 회원가입 처리
     *
     * @param request 회원가입 요청 데이터
     * @return Map<String, String> JWT Access/Refresh Token 응답
     */
    @Transactional
    public Map<String, String> signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 사용자 정보 저장
        User newUser = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .provider(request.getProvider())
                .build();
        userRepository.save(newUser);

        // JWT 발급
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", request.getEmail());
        claims.put("provider", request.getProvider());

        String accessToken = jwtTokenProvider.generateAccessToken(claims);
        String refreshToken = jwtTokenProvider.generateRefreshToken(claims);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
    }

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
