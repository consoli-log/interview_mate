package com.interviewmate.be.prompt.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * packageName    : com.interviewmate.be.prompt.dto
 * fileName       : PromptResponse
 * author         : eumsoli
 * date           : 2025-03-24
 * description    : 프롬프트 저장 응답 DTO
 */
@Schema(description = "프롬프트 저장 응답 DTO")
public record PromptResponse(

        @Schema(description = "프롬프트 ID", example = "1")
        Long id,

        @Schema(description = "프롬프트 제목", example = "개발자 면접 질문")
        String title,

        @Schema(description = "사용자 입력 프롬프트", example = "개발자 면접 대비용 질문을 만들어줘")
        String prompt,

        @Schema(description = "프롬프트 생성 일시", example = "2025-04-01T12:34:56")
        LocalDateTime createdAt

) {}
