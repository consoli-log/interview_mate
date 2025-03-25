package com.interviewmate.be.question.dto;

/**
 * packageName    : com.interviewmate.be.question.dto
 * fileName       : QuestionResponse
 * author         : eumsoli
 * date           : 2025-03-25
 * description    : Gemini 기반 생성된 질문 응답 DTO
 */
public record QuestionResponse(

        int number,      // 프롬프트 내에서의 질문 번호 (1~5, 또는 6 이후)
        String question  // 질문 내용

) {}
