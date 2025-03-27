package com.interviewmate.be.question.dto;

import java.time.LocalDateTime;

/**
 * packageName    : com.interviewmate.be.question.dto
 * fileName       : PromptListResponse
 * author         : eumsoli
 * date           : 2025-03-27
 * description    : 프롬프트 목록 조회 응답 DTO
 */
public record PromptListResponse(

        Long promptId, // 프롬프트 ID
        String title, // 프롬프트 제목
        String prompt, // 프롬프트
        LocalDateTime createdAt // 프롬프트 작성일

) {}
