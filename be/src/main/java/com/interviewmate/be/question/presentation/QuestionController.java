package com.interviewmate.be.question.presentation;

import com.interviewmate.be.auth.domain.User;
import com.interviewmate.be.common.exception.CustomException;
import com.interviewmate.be.common.exception.ErrorCode;
import com.interviewmate.be.question.application.QuestionService;
import com.interviewmate.be.question.dto.QuestionGenerateRequest;
import com.interviewmate.be.question.dto.QuestionListResponse;
import com.interviewmate.be.question.dto.QuestionRegenerateRequest;
import com.interviewmate.be.question.dto.QuestionResponse;
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
public class QuestionController {

    private final QuestionService questionService;

    /**
     * methodName : generateQuestions
     * description : 프롬프트를 기반으로 Gemini 질문 생성
     *               회원은 질문/프롬프트 저장, 비회원은 질문 생성만 수행
     *
     * @param request 프롬프트 요청 DTO
     * @param user    로그인 사용자 (비회원일 경우 null)
     * @return ResponseEntity<List<QuestionResponse>> 생성된 질문 리스트
     */
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
     * description : 특정 질문을 비활성화(삭제) 처리
     *
     * @param questionId 질문 ID (PathVariable)
     * @param user       로그인 사용자
     * @return ResponseEntity<Void> 204 No Content 반환
     * @throws CustomException 질문이 존재하지 않거나 권한이 없는 경우
     */
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
     * description : 비활성화된 질문이 존재하면 새 질문을 하나 생성
     *
     * @param request 재생성 요청 DTO
     * @param user    로그인 사용자
     * @return ResponseEntity<QuestionResponse> 새로 생성된 질문 응답
     */
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
     * description : 특정 프롬프트의 활성 질문들을 조회
     *
     * @param promptId 프롬프트 ID
     * @param user     로그인 사용자
     * @return ResponseEntity<QuestionListResponse> 질문 목록 응답
     */
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
