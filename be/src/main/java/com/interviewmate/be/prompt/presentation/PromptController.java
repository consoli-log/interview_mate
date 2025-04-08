package com.interviewmate.be.prompt.presentation;

import com.interviewmate.be.auth.domain.User;
import com.interviewmate.be.common.exception.CustomException;
import com.interviewmate.be.common.exception.ErrorCode;
import com.interviewmate.be.prompt.application.PromptService;
import com.interviewmate.be.prompt.dto.PromptListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * packageName    : com.interviewmate.be.prompt.presentation
 * fileName       : PromptController
 * author         : eumsoli
 * date           : 2025-03-24
 * description    : 프롬프트 관련 API를 제공하는 컨트롤러
 */
@RestController
@RequestMapping("/api/prompts")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Prompt", description = "프롬프트 관련 API")
public class PromptController {

    private final PromptService promptService;

    /**
     * methodName : getPromptList
     * description : 로그인 사용자의 프롬프트 목록 조회 API
     *
     * @param user 로그인 사용자
     * @return ResponseEntity<List<PromptListResponse>> 프롬프트 목록 응답
     */
    @Operation(
            summary = "프롬프트 목록 조회",
            description = "로그인 사용자의 프롬프트 목록을 조회한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "프롬프트 목록 조회 성공"),
                    @ApiResponse(responseCode = "401", description = "로그인 필요")
            }
    )
    @GetMapping
    public ResponseEntity<List<PromptListResponse>> getPromptList(
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            throw new CustomException(ErrorCode.LOGIN_REQUIRED);
        }

        List<PromptListResponse> response = promptService.getPromptList(user);

        return ResponseEntity.ok(response);
    }

    /**
     * methodName : deactivatePrompt
     * description : 프롬프트 및 연결된 질문 비활성화 API
     *
     * @param promptId 프롬프트 ID
     * @param user 로그인 사용자
     * @return ResponseEntity<Void> 204 No Content 응답
     */
    @Operation(
            summary = "프롬프트 삭제(비활성화)",
            description = "프롬프트 및 연결된 질문들을 비활성화 처리합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "프롬프트 삭제 성공"),
                    @ApiResponse(responseCode = "400", description = "이미 비활성화 상태"),
                    @ApiResponse(responseCode = "401", description = "로그인 필요"),
                    @ApiResponse(responseCode = "403", description = "다른 사용자의 프롬프트"),
                    @ApiResponse(responseCode = "404", description = "프롬프트를 찾을 수 없음")
            }
    )
    @DeleteMapping("/{promptId}")
    public ResponseEntity<Void> deactivatePrompt(
            @PathVariable Long promptId,
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            throw new CustomException(ErrorCode.LOGIN_REQUIRED);
        }

        promptService.deactivatePrompt(promptId, user);

        return ResponseEntity.noContent().build();
    }

}
