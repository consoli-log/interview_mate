package com.interviewmate.be.prompt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * packageName    : com.interviewmate.be.prompt.dto
 * fileName       : PromptRequest
 * author         : eumsoli
 * date           : 2025-03-24
 * description    : 프롬프트 저장 요청 DTO
 */
@Schema(description = "프롬프트 저장 요청 DTO")
public record PromptRequest(

        @Schema(description = "사용자가 입력한 프롬프트", example = "개발자 면접 대비용 질문을 만들어줘")
        @NotBlank(message = "프롬프트는 필수입니다.")
        String prompt // 사용자 입력 프롬프트

) {}
