package com.interviewmate.be.auth.presentation;

import com.interviewmate.be.auth.application.TokenService;
import com.interviewmate.be.auth.application.UserService;
import com.interviewmate.be.auth.domain.User;
import com.interviewmate.be.common.exception.CustomException;
import com.interviewmate.be.common.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * packageName    : com.interviewmate.be.auth.presentation
 * fileName       : UserController
 * author         : eumsoli
 * date           : 2025-03-21
 * description    : 사용자 관련 API를 제공하는 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final TokenService tokenService;

    /**
     * methodName : getCurrentUser
     * description : 현재 로그인된 사용자 정보 조회 API
     *
     * @param user 현재 로그인된 사용자 (@AuthenticationPrincipal 로 주입)
     * @return 사용자 정보 (email, name, providerId, provider)
     */
    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "현재 로그인된 사용자 정보 조회",
            description = "JWT Access Token을 기반으로 현재 로그인한 사용자의 정보를 반환합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공"),
                    @ApiResponse(responseCode = "401", description = "JWT 토큰이 유효하지 않음")
            }
    )
    public ResponseEntity<Map<String, Object>> getCurrentUser(@AuthenticationPrincipal User user) {
        if (user == null) {
            log.warn("인증되지 않은 사용자 정보 요청");
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        log.info("현재 사용자 정보 조회: providerId={}", user.getProviderId());

        return ResponseEntity.ok(Map.of(
                "email", user.getEmail(),
                "name", user.getName(),
                "providerId", user.getProviderId(),
                "provider", user.getProvider()
        ));
    }

    /**
     * methodName : deleteUser
     * description : 회원 탈퇴 API - 현재 로그인된 사용자를 DB에서 삭제하고, Refresh Token도 Redis에서 제거
     *
     * @param user 현재 로그인된 사용자
     * @return 204 No Content
     */
    @DeleteMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "회원 탈퇴",
            description = """
                        현재 로그인한 사용자를 DB에서 삭제하고, Redis에서 해당 사용자의 Refresh Token을 제거합니다.  
                        클라이언트는 응답을 받으면 Access Token과 Refresh Token을 삭제하고 로그인 화면으로 이동해야 합니다. 
                        """,
            responses = {
                    @ApiResponse(responseCode = "204", description = "회원 탈퇴 및 로그아웃 성공"),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
            }
    )
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal User user) {
        if (user == null) {
            log.warn("회원 탈퇴 요청: 인증되지 않은 사용자");
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        log.info("회원 탈퇴 요청: providerId={}", user.getProviderId());

        userService.deleteUser(user); // DB에서 삭제
        tokenService.deleteRefreshToken(user.getProviderId()); // Redis에서 삭제

        return ResponseEntity.noContent().build();
    }

}
