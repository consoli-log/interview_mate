package com.interviewmate.be.question.application;

import com.interviewmate.be.auth.domain.User;
import com.interviewmate.be.infrastructure.openai.GeminiClient;
import com.interviewmate.be.infrastructure.persistence.question.PromptRepository;
import com.interviewmate.be.infrastructure.persistence.question.QuestionRepository;
import com.interviewmate.be.question.domain.Prompt;
import com.interviewmate.be.question.domain.Question;
import com.interviewmate.be.question.dto.QuestionGenerateRequest;
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
                .map(question -> new QuestionResponse(question.getNumber(), question.getQuestion()))
                .toList();
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
