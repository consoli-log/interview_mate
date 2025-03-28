package com.interviewmate.be.question.application;

import com.interviewmate.be.auth.domain.User;
import com.interviewmate.be.common.exception.CustomException;
import com.interviewmate.be.common.exception.ErrorCode;
import com.interviewmate.be.infrastructure.persistence.question.PromptRepository;
import com.interviewmate.be.infrastructure.persistence.question.QuestionRepository;
import com.interviewmate.be.question.domain.Prompt;
import com.interviewmate.be.question.domain.Question;
import com.interviewmate.be.question.dto.PromptListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * packageName    : com.interviewmate.be.question.application
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
     * description : 프롬프트 저장 및 요약 제목 생성 처리
     *
     * @param user 사용자
     * @param promptContent 사용자 입력 프롬프트
     * @return Prompt 저장된 프롬프트 엔티티
     */
    @Transactional
    public Prompt savePrompt(User user, String promptContent) {
        String title = summarizeTitle(promptContent);

        Prompt prompt = Prompt.builder()
                .user(user)
                .title(title)
                .prompt(promptContent)
                .build();

        return promptRepository.save(prompt);
    }

    /**
     * methodName : summarizeTitle
     * description : 임시 제목 생성 (앞 20자 잘라내기)
     *
     * @param prompt 프롬프트 내용
     * @return 요약된 제목
     */
    private String summarizeTitle(String prompt) {
        return prompt.length() > 20 ? prompt.substring(0, 20) + "..." : prompt;
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
            throw new CustomException(ErrorCode.PROMPT_NOT_OWNED);
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
