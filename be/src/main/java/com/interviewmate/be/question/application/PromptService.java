package com.interviewmate.be.question.application;

import com.interviewmate.be.auth.domain.User;
import com.interviewmate.be.infrastructure.persistence.question.PromptRepository;
import com.interviewmate.be.question.domain.Prompt;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}
