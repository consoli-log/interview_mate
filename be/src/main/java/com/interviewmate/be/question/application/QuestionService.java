package com.interviewmate.be.question.application;

import com.interviewmate.be.auth.domain.User;
import com.interviewmate.be.common.exception.CustomException;
import com.interviewmate.be.common.exception.ErrorCode;
import com.interviewmate.be.infrastructure.openai.GeminiClient;
import com.interviewmate.be.infrastructure.persistence.prompt.PromptRepository;
import com.interviewmate.be.infrastructure.persistence.question.QuestionRepository;
import com.interviewmate.be.prompt.application.PromptService;
import com.interviewmate.be.prompt.domain.Prompt;
import com.interviewmate.be.question.domain.Question;
import com.interviewmate.be.question.dto.QuestionGenerateRequest;
import com.interviewmate.be.question.dto.QuestionListResponse;
import com.interviewmate.be.question.dto.QuestionRegenerateRequest;
import com.interviewmate.be.question.dto.QuestionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * packageName    : com.interviewmate.be.question.application
 * fileName       : QuestionService
 * author         : eumsoli
 * date           : 2025-03-25
 * description    : Gemini 기반 질문 생성을 담당하는 서비스
 */
@Service
@RequiredArgsConstructor
public class QuestionService {

    private final GeminiClient geminiClient;
    private final PromptService promptService;
    private final QuestionRepository questionRepository;
    private final PromptRepository promptRepository;

    /**
     * methodName : generateQuestions
     * description : Gemini API를 통해 질문을 생성하고, 회원 여부에 따라 저장 처리
     *
     * @param request 질문 생성 요청 DTO
     * @param user    로그인 사용자 (null이면 비회원)
     * @return List<QuestionResponse> 질문 리스트 응답
     */
    @Transactional
    public List<QuestionResponse> generateQuestions(QuestionGenerateRequest request, User user) {
        // 질문 생성
        List<String> generated = geminiClient.generateQuestions(request.prompt());

        // 비회원일 경우
        if(user == null) {
            return toResponse(generated, 1); // 저장 없이 그대로 응답만
        }

        // 회원일 경우
        // 1. 프롬프트 저장
        Prompt prompt = promptService.savePrompt(user, request.prompt());

        // 2. 현재 질문 번호 채번
        int maxNumber = questionRepository.findMaxNumberByPrompt(prompt);
        int startNumber = maxNumber + 1;

        // 3. 질문 저장
        List<Question> saved = new ArrayList<>();

        for(int i = 0; i < generated.size(); i++) {
            Question question = Question.builder()
                    .prompt(prompt)
                    .number(startNumber + i)
                    .question(generated.get(i))
                    .build();

            saved.add(questionRepository.save(question));
        }

        return saved.stream()
                .map(q -> new QuestionResponse(q.getNumber(), q.getQuestion()))
                .toList();
    }

    /**
     * methodName : deactivateQuestion
     * description : 질문을 삭제(비활성화) 처리한다.
     *
     * @param questionId 질문 ID
     * @param user       로그인 사용자
     * @throws CustomException 질문이 없거나 권한이 없거나 이미 비활성화된 경우
     */
    @Transactional
    public void deactivateQuestion(Long questionId, User user) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

        // 사용자 자신의 프롬프트인 지 확인
        if (!question.getPrompt().getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.QUESTION_ACCESS_DENIED);
        }

        // 이미 비활성화 되었는 지 확인
        if (!question.isActive()) {
            throw new CustomException(ErrorCode.QUESTION_ALREADY_DEACTIVATED);
        }

        question.deactivate();
    }

    /**
     * methodName : regenerateQuestion
     * description : 비활성화된 질문이 존재할 경우, 새 질문을 하나 생성한다.
     *
     * @param request 질문 재생성 요청 DTO
     * @param user    로그인 사용자
     * @return QuestionResponse 생성된 질문 응답
     */
    @Transactional
    public QuestionResponse regenerateQuestion(QuestionRegenerateRequest request, User user) {
        Prompt prompt = promptRepository.findById(request.promptId())
                .orElseThrow(() -> new CustomException(ErrorCode.PROMPT_NOT_FOUND));

        if (!prompt.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.QUESTION_ACCESS_DENIED);
        }

        // 비활성화된 질문이 존재하는지 확인
        boolean hasInactive = questionRepository.existsByPromptAndIsActiveFalse(prompt);

        if (!hasInactive) {
            throw new CustomException(ErrorCode.QUESTION_REGENERATION_NOT_ALLOWED);
        }

        // 새로운 질문 생성
        String newQuestion = geminiClient.generateSingleQuestion(prompt.getPrompt());

        int maxNumber = questionRepository.findMaxNumberByPrompt(prompt);
        Question question = Question.builder()
                .prompt(prompt)
                .question(newQuestion)
                .number(maxNumber + 1)
                .build();

        Question saved = questionRepository.save(question);
        return new QuestionResponse(saved.getNumber(), saved.getQuestion());
    }

    /**
     * methodName : getActiveQuestions
     * description : 특정 프롬프트의 활성 질문들을 조회
     *
     * @param promptId 프롬프트 ID
     * @param user     로그인 사용자
     * @return QuestionListResponse 질문 목록 응답
     */
    @Transactional(readOnly = true)
    public QuestionListResponse getActiveQuestions(Long promptId, User user) {
        Prompt prompt = promptRepository.findById(promptId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROMPT_NOT_FOUND));

        if (!prompt.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.QUESTION_ACCESS_DENIED);
        }

        List<Question> questions = questionRepository.findAllByPromptAndIsActiveTrueOrderByNumber(prompt);

        List<QuestionResponse> responseList = questions.stream()
                .map(q -> new QuestionResponse(q.getNumber(), q.getQuestion()))
                .toList();

        return new QuestionListResponse(responseList);
    }

    /**
     * methodName : toResponse
     * description : 질문 리스트를 응답 DTO로 변환
     *
     * @param questions    생성된 질문 리스트
     * @param startNumber  시작 번호
     * @return List<QuestionResponse>
     */
    private List<QuestionResponse> toResponse(List<String> questions, int startNumber) {
        List<QuestionResponse> questionResponses = new ArrayList<>();

        for(int i = 0; i < questions.size(); i++) {
            questionResponses.add(new QuestionResponse(startNumber + i, questions.get(i)));
        }

        return questionResponses;
    }

}
