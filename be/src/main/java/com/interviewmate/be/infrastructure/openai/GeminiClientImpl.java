package com.interviewmate.be.infrastructure.openai;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

/**
 * packageName    : com.interviewmate.be.infrastructure.openai
 * fileName       : GeminiClientImpl
 * author         : eumsoli
 * date           : 2025-03-26
 * description    : Gemini API와 연동하여 질문을 생성하는 클라이언트 구현체 (현재는 Mock)
 */
@Component
public class GeminiClientImpl implements GeminiClient {

    private static final List<String> MOCK_QUESTIONS = List.of(
            "이 직무에 지원하게 된 계기는 무엇인가요?",
            "본인의 강점 중 이 직무에 가장 잘 맞는 부분은 무엇인가요?",
            "최근 경험 중 문제를 해결한 사례를 설명해주세요.",
            "팀 내 갈등을 어떻게 해결하셨나요?",
            "단기간에 새로운 기술을 배운 경험이 있다면 말씀해주세요."
    );

    private final Random random = new Random();

    /**
     * methodName : generateQuestions
     * description : 프롬프트 기반 질문 5개 생성 (Mock)
     *
     * @param prompt 프롬프트 내용
     * @return List<String> 생성된 질문 리스트 (5개 고정)
     */
    @Override
    public List<String> generateQuestions(String prompt) {
        // TODO 실제 연동 시 prompt를 기반으로 Gemini API 호출
        return MOCK_QUESTIONS;
    }

    /**
     * methodName : generateSingleQuestion
     * description : 프롬프트 기반 질문 1개를 생성 (Mock)
     *
     * @param prompt 프롬프트 내용
     * @return String 생성된 질문 1개
     */
    @Override
    public String generateSingleQuestion(String prompt) {
        // TODO 실제 연동 시 prompt 기반으로 Gemini API 호출 후 1개만 파싱
        return MOCK_QUESTIONS.get(random.nextInt(MOCK_QUESTIONS.size()));
    }
}
