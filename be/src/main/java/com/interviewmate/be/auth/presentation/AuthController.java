package com.interviewmate.be.auth.presentation;

import com.interviewmate.be.auth.application.AuthService;
import com.interviewmate.be.common.exception.CustomException;
import com.interviewmate.be.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * packageName    : com.interviewmate.be.auth.presentation
 * fileName       : AuthController
 * author         : eumsoli
 * date           : 2025-03-08
 * description    : JWT 기반 인증 관련 API를 제공하는 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * methodName : refreshToken
     * description :  - Refresh Token을 이용하여 새로운 Access Token 발급 API
     *
     * @param requestBody 요청 본문(JSON) { "refreshToken": "..." }
     * @return 새로운 Access Token
     */
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestBody Map<String, String> requestBody) {
        String refreshToken = requestBody.get("refreshToken");

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        String newAccessToken = authService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    /**
     * methodName : logout
     * description : 로그아웃 API - Redis에서 Refresh Token 삭제
     *
     * @param user 인증된 사용자 정보 (providerId 기반)
     * @return 204 No Content (성공)
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal User user) {
        if (user == null) {
            log.warn("로그아웃 요청: 인증된 사용자 없음");
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        log.info("로그아웃 요청: providerId={}", user.getUsername());
        authService.logout(user.getUsername());  // providerId 기반으로 Refresh Token 삭제
        return ResponseEntity.noContent().build(); // 204 No Content 응답
    }

}
