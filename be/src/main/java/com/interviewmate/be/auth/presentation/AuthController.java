package com.interviewmate.be.auth.presentation;

import com.interviewmate.be.auth.application.AuthService;
import com.interviewmate.be.auth.dto.RefreshTokenRequest;
import com.interviewmate.be.common.exception.CustomException;
import com.interviewmate.be.common.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
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
     * description : Refresh Token을 이용하여 새로운 Access Token 발급 API
     *
     * @param request Refresh Token 요청 DTO
     * @return 새로운 Access Token
     */
    @PostMapping("/refresh")
    @Operation(
            summary = "Access Token 재발급",
            description = "Refresh Token을 이용하여 새로운 Access Token을 발급합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Access Token 재발급 성공"),
                    @ApiResponse(responseCode = "400", description = "Refresh Token이 제공되지 않음"),
                    @ApiResponse(responseCode = "401", description = "유효하지 않은 Refresh Token"),
                    @ApiResponse(responseCode = "404", description = "Refresh Token이 존재하지 않음")
            }
    )
    public ResponseEntity<Map<String, String>> refreshToken(
            @RequestBody(description = "Refresh Token 요청 객체") RefreshTokenRequest request) {
        if (request.getRefreshToken() == null || request.getRefreshToken().isBlank()) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        String newAccessToken = authService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    /**
     * methodName : logout
     * description : 로그아웃 API - Redis에서 Refresh Token 삭제
     *
     * @param user 인증된 사용자 정보 (providerId 기반)
     */
    @PostMapping("/logout")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "로그아웃",
            description = """
                        현재 사용자의 Refresh Token을 삭제하여 로그아웃 처리합니다.  
                        클라이언트는 응답을 받으면 Access Token 및 Refresh Token을 삭제해야 합니다.
                        """,
            responses = {
                    @ApiResponse(responseCode = "204", description = "로그아웃 성공"),
                    @ApiResponse(responseCode = "400", description = "Refresh Token이 제공되지 않음"),
                    @ApiResponse(responseCode = "401", description = "유효하지 않은 Refresh Token")
            }
    )
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
