package com.interviewmate.be.question.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * packageName    : com.interviewmate.be.question.dto
 * fileName       : QuestionRegenerateRequest
 * author         : eumsoli
 * date           : 2025-03-26
 * description    : 질문 재생성 요청 DTO
 */
@Schema(description = "질문 재생성 요청 DTO")
public record QuestionRegenerateRequest(

        @Schema(description = "프롬프트 ID", example = "1")
        @NotNull(message = "프롬프트 ID는 필수입니다.")
        Long promptId // 재생성할 프롬프트

) {}
