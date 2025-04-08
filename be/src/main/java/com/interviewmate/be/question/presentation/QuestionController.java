package com.interviewmate.be.question.presentation;

import com.interviewmate.be.auth.domain.User;
import com.interviewmate.be.common.exception.CustomException;
import com.interviewmate.be.common.exception.ErrorCode;
import com.interviewmate.be.question.application.QuestionService;
import com.interviewmate.be.question.dto.QuestionGenerateRequest;
import com.interviewmate.be.question.dto.QuestionListResponse;
import com.interviewmate.be.question.dto.QuestionRegenerateRequest;
import com.interviewmate.be.question.dto.QuestionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * packageName    : com.interviewmate.be.question.presentation
 * fileName       : QuestionController
 * author         : eumsoli
 * date           : 2025-03-25
 * description    : 질문 관련 API를 제공하는 컨트롤러
 */
@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
@Tag(name = "Question", description = "질문 관련 API")
public class QuestionController {

    private final QuestionService questionService;

    /**
     * methodName : generateQuestions
     * description : 프롬프트를 기반으로 Gemini 질문 생성 API
     *               회원은 질문/프롬프트 저장, 비회원은 질문 생성만 수행
     *
     * @param request 프롬프트 요청 DTO
     * @param user    로그인 사용자 (비회원일 경우 null)
     * @return ResponseEntity<List<QuestionResponse>> 생성된 질문 리스트
     */
    @Operation(
            summary = "질문 생성",
            description = "프롬프트를 기반으로 5개의 질문을 생성합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "질문 생성 성공"),
                    @ApiResponse(responseCode = "500", description = "Gemini API 호출 실패")
            }
    )
    @PostMapping("/generate")
    public ResponseEntity<List<QuestionResponse>> generateQuestions(
            @RequestBody QuestionGenerateRequest request,
            @AuthenticationPrincipal User user
    ) {
        List<QuestionResponse> responses = questionService.generateQuestions(request, user);

        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    /**
     * methodName : deactivateQuestion
     * description : 특정 질문 비활성화(삭제) API
     *
     * @param questionId 질문 ID
     * @param user       로그인 사용자
     * @return ResponseEntity<Void> 204 No Content 반환
     * @throws CustomException 질문이 존재하지 않거나 권한이 없는 경우
     */
    @Operation(
            summary = "질문 삭제(비활성화)",
            description = "특정 질문을 비활성화 처리합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "질문 삭제 성공"),
                    @ApiResponse(responseCode = "400", description = "이미 비활성화 상태"),
                    @ApiResponse(responseCode = "401", description = "로그인 필요"),
                    @ApiResponse(responseCode = "403", description = "다른 사용자의 질문"),
                    @ApiResponse(responseCode = "404", description = "질문을 찾을 수 없음")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> deactivateQuestion(
            @PathVariable Long questionId,
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            throw new CustomException(ErrorCode.LOGIN_REQUIRED);
        }

        questionService.deactivateQuestion(questionId, user);

        return ResponseEntity.noContent().build();
    }

    /**
     * methodName : regenerateQuestion
     * description : 비활성화된 질문 존재 시 새 질문 하나 생성 API
     *
     * @param request 재생성 요청 DTO
     * @param user    로그인 사용자
     * @return ResponseEntity<QuestionResponse> 새로 생성된 질문 응답
     */
    @Operation(
            summary = "질문 재생성",
            description = "비활성화된 질문이 있을 경우 새 질문을 1개 재생성합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "질문 재생성 성공"),
                    @ApiResponse(responseCode = "400", description = "모든 질문이 활성 상태"),
                    @ApiResponse(responseCode = "401", description = "로그인 필요"),
                    @ApiResponse(responseCode = "403", description = "다른 사용자의 프롬프트"),
                    @ApiResponse(responseCode = "404", description = "프롬프트를 찾을 수 없음"),
                    @ApiResponse(responseCode = "500", description = "Gemini API 호출 실패")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/regenerate")
    public ResponseEntity<QuestionResponse> regenerateQuestion(
            @RequestBody QuestionRegenerateRequest request,
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            throw new CustomException(ErrorCode.LOGIN_REQUIRED);
        }

        QuestionResponse response = questionService.regenerateQuestion(request, user);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * methodName : getQuestions
     * description : 특정 프롬프트의 활성 질문 목록 조회 API
     *
     * @param promptId 프롬프트 ID
     * @param user     로그인 사용자
     * @return ResponseEntity<QuestionListResponse> 질문 목록 응답
     */
    @Operation(
            summary = "질문 목록 조회",
            description = "프롬프트 ID에 해당하는 활성화된 질문 목록을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "질문 목록 조회 성공"),
                    @ApiResponse(responseCode = "401", description = "로그인 필요"),
                    @ApiResponse(responseCode = "403", description = "다른 사용자의 프롬프트"),
                    @ApiResponse(responseCode = "404", description = "프롬프트를 찾을 수 없음")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/prompt/{promptId}")
    public ResponseEntity<QuestionListResponse> getQuestions(
            @PathVariable Long promptId,
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            throw new CustomException(ErrorCode.LOGIN_REQUIRED);
        }

        QuestionListResponse response = questionService.getActiveQuestions(promptId, user);

        return ResponseEntity.ok(response);
    }

}
