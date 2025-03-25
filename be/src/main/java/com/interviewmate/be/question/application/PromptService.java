package com.interviewmate.be.question.application;

import com.interviewmate.be.infrastructure.persistence.question.PromptRepository;
import com.interviewmate.be.question.domain.Prompt;
import com.interviewmate.be.question.dto.PromptRequest;
import com.interviewmate.be.question.dto.PromptResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * packageName    : com.interviewmate.be.question.application
 * fileName       : PromptService
 * author         : eumsoli
 * date           : 2025-03-24
 * description    : 프롬프트 저장 비즈니스 로직을 담당하는 서비스
 */
@Service
@RequiredArgsConstructor
public class PromptService {

    private final PromptRepository promptRepository;

    /**
     * methodName : savePrompt
     * description : 프롬프트를 저장하고 임시 제목을 생성한다.
     *
     * @param promptRequest Prompt 저장 요청 DTO
     * @return Prompt 저장 응답 DTO
     */
    @Transactional
    public PromptResponse savePrompt(PromptRequest promptRequest) {
        // TODO Gemini API 연동 후 생성된 요약 제목으로 대체
        String tempTitle = summarizeTitle(promptRequest.prompt());

        Prompt prompt = Prompt.builder()
                .title(tempTitle)
                .prompt(promptRequest.prompt())
                .build();

        Prompt saved = promptRepository.save(prompt);

        return new PromptResponse(saved.getId(), saved.getTitle(), saved.getPrompt(), saved.getCreatedAt());
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
