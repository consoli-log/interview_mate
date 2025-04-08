package com.interviewmate.be.question.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * packageName    : com.interviewmate.be.question.dto
 * fileName       : QuestionGenerateRequest
 * author         : eumsoli
 * date           : 2025-03-25
 * description    : 질문 생성 요청 DTO
 */
@Schema(description = "질문 생성 요청 DTO")
public record QuestionGenerateRequest(

        @Schema(description = "프롬프트 내용", example = "자기소개서 내용을 입력해주세요")
        @NotBlank(message = "프롬프트는 필수입니다.")
        String prompt // 사용자 입력 프롬프트 (질문 생성 기준)

) {}
