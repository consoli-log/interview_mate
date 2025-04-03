package com.interviewmate.be.question.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * packageName    : com.interviewmate.be.question.dto
 * fileName       : QuestionResponse
 * author         : eumsoli
 * date           : 2025-03-25
 * description    : 생성된 질문 응답 DTO
 */
@Schema(description = "질문 응답 DTO")
public record QuestionResponse(

        @Schema(description = "질문 번호", example = "1")
        int number,      // 프롬프트 내에서의 질문 번호 (1~5, 또는 6 이후)

        @Schema(description = "질문 내용", example = "이 프로젝트에서 맡은 역할은 무엇인가요?")
        String question  // 질문 내용

) {}
