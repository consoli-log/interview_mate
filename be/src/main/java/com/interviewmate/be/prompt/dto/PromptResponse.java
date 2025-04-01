package com.interviewmate.be.prompt.dto;

import java.time.LocalDateTime;

/**
 * packageName    : com.interviewmate.be.prompt.dto
 * fileName       : PromptResponse
 * author         : eumsoli
 * date           : 2025-03-24
 * description    : 프롬프트 저장 응답 DTO
 */
public record PromptResponse(

        Long id,
        String title,
        String prompt,
        LocalDateTime createdAt

) {}
