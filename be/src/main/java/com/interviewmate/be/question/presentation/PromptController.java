package com.interviewmate.be.question.presentation;

import com.interviewmate.be.question.application.PromptService;
import com.interviewmate.be.question.dto.PromptRequest;
import com.interviewmate.be.question.dto.PromptResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * packageName    : com.interviewmate.be.question.presentation
 * fileName       : PromptController
 * author         : eumsoli
 * date           : 2025-03-24
 * description    : 프롬프트 관련 API를 제공하는 컨트롤러
 */
@RestController
@RequestMapping("/api/prompts")
@RequiredArgsConstructor
public class PromptController {

    private final PromptService promptService;

    /**
     * methodName : savePrompt
     * description : 프롬프트 저장 API
     *
     * @param promptRequest Prompt 저장 요청 DTO
     * @return ResponseEntity<PromptResponse> 저장된 프롬프트 응답
     */
    @PostMapping
    public ResponseEntity<PromptResponse> savePrompt(@RequestBody PromptRequest promptRequest) {
        PromptResponse promptResponse = promptService.savePrompt(promptRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(promptResponse);
    }

}
