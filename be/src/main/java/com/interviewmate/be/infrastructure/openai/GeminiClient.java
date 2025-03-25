package com.interviewmate.be.infrastructure.openai;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * packageName    : com.interviewmate.be.infrastructure.openai
 * fileName       : GeminiClient
 * author         : eumsoli
 * date           : 2025-03-25
 * description    : Gemini API를 통해 프롬프트 기반 질문을 생성하는 클라이언트
 */
@Component
public class GeminiClient {

    /**
     * methodName : generateQuestions
     * description : 프롬프트를 기반으로 질문 5개를 생성 (임시 Mock 구현)
     *
     * @param prompt 사용자가 입력한 프롬프트
     * @return List<String> 생성된 질문 리스트
     */
    public List<String> generateQuestions(String prompt) {
        // TODO: 이후 Gemini API 연동으로 대체 예정
        List<String> questions = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            questions.add(i + "번 질문: [" + prompt + "] 에 대한 질문 " + i);
        }
        return questions;
    }

}
