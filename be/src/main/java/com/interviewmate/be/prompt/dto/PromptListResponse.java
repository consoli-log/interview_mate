package com.interviewmate.be.prompt.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * packageName    : com.interviewmate.be.prompt.dto
 * fileName       : PromptListResponse
 * author         : eumsoli
 * date           : 2025-03-27
 * description    : 프롬프트 목록 조회 응답 DTO
 */
@Schema(description = "프롬프트 목록 조회 응답 DTO")
public record PromptListResponse(

        @Schema(description = "프롬프트 ID", example = "1")
        Long promptId, // 프롬프트 ID

        @Schema(description = "프롬프트 제목", example = "AI 면접 질문 생성")
        String title, // 프롬프트 제목

        @Schema(description = "프롬프트 내용", example = "AI를 활용해 자기소개 질문 5개를 만들어줘")
        String prompt, // 프롬프트

        @Schema(description = "프롬프트 생성일", example = "2025-03-27T15:00:00")
        LocalDateTime createdAt // 프롬프트 작성일

) {}
