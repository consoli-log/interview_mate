package com.interviewmate.be.infrastructure.openai;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * packageName    : com.interviewmate.be.infrastructure.openai
 * fileName       : GeminiClient
 * author         : eumsoli
 * date           : 2025-03-25
 * description    : Gemini API와 연동하여 질문을 생성하는 클라이언트
 */
@Component
public interface GeminiClient {

    /**
     * methodName : generateQuestions
     * description : 프롬프트를 기반으로 질문 5개를 생성
     *
     * @param prompt 프롬프트 내용
     * @return List<String> 생성된 질문 리스트
     */
    List<String> generateQuestions(String prompt);

    /**
     * methodName : generateSingleQuestion
     * description : 프롬프트를 기반으로 질문 1개를 생성
     *
     * @param prompt 프롬프트 내용
     * @return String 생성된 질문 1개
     */
    String generateSingleQuestion(String prompt);

}
