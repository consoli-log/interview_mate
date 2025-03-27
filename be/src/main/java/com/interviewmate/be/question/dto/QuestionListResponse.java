package com.interviewmate.be.question.dto;

import java.util.List;

/**
 * packageName    : com.interviewmate.be.question.dto
 * fileName       : QuestionListResponse
 * author         : eumsoli
 * date           : 2025-03-26
 * description    : 질문 목록 조회 응답 DTO
 */
public record QuestionListResponse(

        List<QuestionResponse> questions // 질문 목록

) {}
