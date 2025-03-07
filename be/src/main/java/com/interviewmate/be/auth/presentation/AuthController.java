package com.interviewmate.be.auth.presentation;

import com.interviewmate.be.auth.application.AuthService;
import com.interviewmate.be.common.exception.CustomException;
import com.interviewmate.be.common.exception.ErrorCode;
import com.interviewmate.be.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * packageName    : com.interviewmate.be.auth.presentation
 * fileName       : AuthController
 * author         : eumsoli
 * date           : 2025-03-08
 * description    : OAuth2 로그인 관련 API 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * methodName : getCurrentUser
     * description : 현재 로그인한 사용자 정보 조회 API
     *
     * @param userDetails 현재 인증된 사용자 정보
     * @return ApiResponse<UserDetails> 사용자 정보 반환
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDetails>> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null || userDetails.getUsername() == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
        return ResponseEntity.ok(ApiResponse.success(userDetails));
    }

    /**
     * methodName : logout
     * description : 로그아웃 처리 API (쿠키 삭제)
     *
     * @param request  HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @return ApiResponse<Void> 로그아웃 성공 응답
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        response.setHeader("Location", "/");
        response.setStatus(302);
        return ResponseEntity.ok(ApiResponse.success());
    }

}
