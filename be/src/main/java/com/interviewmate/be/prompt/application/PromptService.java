package com.interviewmate.be.prompt.application;

import com.interviewmate.be.auth.domain.User;
import com.interviewmate.be.common.exception.CustomException;
import com.interviewmate.be.common.exception.ErrorCode;
import com.interviewmate.be.infrastructure.persistence.prompt.PromptRepository;
import com.interviewmate.be.infrastructure.persistence.question.QuestionRepository;
import com.interviewmate.be.prompt.domain.Prompt;
import com.interviewmate.be.prompt.dto.PromptListResponse;
import com.interviewmate.be.question.domain.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * packageName    : com.interviewmate.be.prompt.application
 * fileName       : PromptService
 * author         : eumsoli
 * date           : 2025-03-24
 * description    : 프롬프트 저장 및 관련 로직을 담당하는 서비스
 */
@Service
@RequiredArgsConstructor
public class PromptService {

    private final PromptRepository promptRepository;
    private final QuestionRepository questionRepository;

    /**
     * methodName : savePrompt
     * description : Gemini로부터 받은 제목을 포함하여 프롬프트 저장
     *
     * @param user 사용자
     * @param promptContent 프롬프트 내용
     * @param title Gemini API로부터 생성된 요약 제목
     * @return 저장된 Prompt 엔티티
     */
    @Transactional
    public Prompt savePrompt(User user, String promptContent, String title) {
        Prompt prompt = Prompt.builder()
                .user(user)
                .prompt(promptContent)
                .title(title)
                .build();

        return promptRepository.save(prompt);
    }

    /**
     * methodName : getPromptList
     * description : 로그인 사용자의 프롬프트 목록을 조회
     *
     * @param user 로그인 사용자
     * @return List<PromptListResponse> 프롬프트 목록 응답 리스트
     */
    @Transactional(readOnly = true)
    public List<PromptListResponse> getPromptList(User user) {
        List<Prompt> prompts = promptRepository.findAllByUserAndIsActiveTrueOrderByCreatedAtDesc(user);

        return prompts.stream()
                .map(prompt -> {
                    return new PromptListResponse(
                            prompt.getId(),
                            prompt.getTitle(),
                            prompt.getPrompt(),
                            prompt.getCreatedAt()
                    );
                })
                .toList();
    }

    /**
     * methodName : deactivatePrompt
     * description : 프롬프트와 연관된 질문들을 함께 비활성화 처리
     *
     * @param promptId 프롬프트 ID
     * @param user 로그인 사용자
     * @throws CustomException 존재하지 않거나 소유자가 다르거나 이미 비활성화된 경우
     */
    @Transactional
    public void deactivatePrompt(Long promptId, User user) {
        Prompt prompt = promptRepository.findById(promptId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROMPT_NOT_FOUND));

        // 사용자 자신의 프롬프트인 지 확인
        if (!prompt.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.PROMPT_ACCESS_DENIED);
        }

        // 이미 비활성화 되었는 지 확인
        if (!prompt.isActive()) {
            throw new CustomException(ErrorCode.PROMPT_ALREADY_DEACTIVATED);
        }

        // 프롬프트 비활성화
        prompt.deactivate();

        // 연결된 질문 모두 비활성화
        List<Question> questions = questionRepository.findAllByPromptAndIsActiveTrue(prompt);
        questions.forEach(Question::deactivate);
    }

}
