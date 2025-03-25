package com.interviewmate.be.question.presentation;

import com.interviewmate.be.auth.domain.User;
import com.interviewmate.be.infrastructure.persistence.auth.UserRepository;
import com.interviewmate.be.question.application.QuestionService;
import com.interviewmate.be.question.dto.QuestionGenerateRequest;
import com.interviewmate.be.question.dto.QuestionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
