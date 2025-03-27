package com.interviewmate.be.question.dto;

/**
 * packageName    : com.interviewmate.be.question.dto
 * fileName       : QuestionRegenerateRequest
 * author         : eumsoli
 * date           : 2025-03-26
 * description    : 질문 재생성 요청 DTO
 */
public record QuestionRegenerateRequest(

        Long promptId // 재생성할 프롬프트

) {}
