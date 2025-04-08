package com.interviewmate.be.infrastructure.openai.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * packageName    : com.interviewmate.be.infrastructure.openai.dto
 * fileName       : GeminiResponse
 * author         : eumsoli
 * date           : 2025-04-07
 * description    : Gemini API 응답 DTO
 */
@Schema(description = "Gemini API 응답 DTO")
public record GeminiResponse(

        @Schema(description = "요약된 제목", example = "갈등 해결 경험")
        String title,

        @Schema(description = "질문 리스트")
        List<QuestionItem> questions

) {

    @Schema(description = "질문 항목 DTO")
    public static record QuestionItem(

            @Schema(description = "면접 질문", example = "갈등 상황에서 팀원과 어떤 방식으로 소통했나요?")
            String question

    ) {}
}
