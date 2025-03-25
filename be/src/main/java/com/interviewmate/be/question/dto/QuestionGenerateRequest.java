package com.interviewmate.be.question.dto;

/**
 * packageName    : com.interviewmate.be.question.dto
 * fileName       : QuestionGenerateRequest
 * author         : eumsoli
 * date           : 2025-03-25
 * description    : Gemini 기반 질문 생성 요청 DTO
 */
public record QuestionGenerateRequest(

        String prompt // 사용자 입력 프롬프트 (질문 생성 기준)

) {}
