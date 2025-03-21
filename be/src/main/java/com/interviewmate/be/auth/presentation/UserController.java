package com.interviewmate.be.auth.presentation;

import com.interviewmate.be.auth.application.TokenService;
import com.interviewmate.be.auth.application.UserService;
import com.interviewmate.be.auth.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final TokenService tokenService;

    public UserController(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    /**
     * methodName : getCurrentUser
     * description : 현재 로그인된 사용자 정보 조회 API
     *
     * @param user 현재 로그인된 사용자 (@AuthenticationPrincipal 로 주입)
     * @return 사용자 정보 (email, name, providerId, provider)
     */
    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "현재 로그인된 사용자 정보 조회", description = "JWT Access Token을 기반으로 현재 로그인한 사용자의 정보를 반환합니다.")
    public Map<String, Object> getCurrentUser(@AuthenticationPrincipal User user) {
        return Map.of(
                "email", user.getEmail(),
                "name", user.getName(),
                "providerId", user.getProviderId(),
                "provider", user.getProvider()
        );
    }

    /**
     * methodName : deleteUser
     * description : 회원 탈퇴 API - 현재 로그인된 사용자를 DB에서 삭제하고, Refresh Token도 Redis에서 제거
     *
     * @param user 현재 로그인된 사용자
     */
    @DeleteMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "회원 탈퇴", description = "현재 로그인된 사용자를 탈퇴 처리하고 자동 로그아웃 처리합니다.")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal User user) {

        userService.deleteUser(user); // DB에서 삭제
        tokenService.deleteRefreshToken(user.getProviderId()); // Redis에서 삭제

        return ResponseEntity.noContent().build();
    }

}
