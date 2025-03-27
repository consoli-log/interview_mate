package com.interviewmate.be.question.application;

import com.interviewmate.be.auth.domain.User;
import com.interviewmate.be.infrastructure.persistence.question.PromptRepository;
import com.interviewmate.be.infrastructure.persistence.question.QuestionRepository;
import com.interviewmate.be.question.domain.Prompt;
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

}
