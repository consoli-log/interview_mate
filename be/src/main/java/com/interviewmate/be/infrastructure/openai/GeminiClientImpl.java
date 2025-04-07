package com.interviewmate.be.infrastructure.openai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewmate.be.common.config.GeminiConfig;
import com.interviewmate.be.common.exception.CustomException;
import com.interviewmate.be.common.exception.ErrorCode;
import com.interviewmate.be.infrastructure.openai.dto.GeminiResponse;
import com.interviewmate.be.infrastructure.openai.dto.GeminiResponse.QuestionItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * packageName    : com.interviewmate.be.infrastructure.openai
 * fileName       : GeminiClientImpl
 * author         : eumsoli
 * date           : 2025-03-26
 * description    : Gemini API와 연동하여 질문을 생성하는 클라이언트 구현체 (현재는 Mock)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiClientImpl implements GeminiClient {

    private final GeminiConfig geminiConfig;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 면접 질문 5개 + 요약 제목 요청용 시스템 프롬프트
    private static final String MULTI_QUESTION_PROMPT = """
        다음 프롬프트를 요약한 제목과 관련된 면접 질문 5개를 JSON 형태로 만들어줘.
        백틱(```) 없이 {"title": "...", "questions": ["질문1", "질문2", ...]} 형태로만 응답해줘.
        """;

    // 단일 질문 요청용 시스템 프롬프트
    private static final String SINGLE_QUESTION_PROMPT = """
        다음 프롬프트를 기반으로 면접 질문 1개를 JSON 형태로 만들어줘.
        백틱(```) 없이 {"question": "질문1"} 형태로만 응답해줘.
        """;

    /**
     * methodName : generateQuestions
     * description : Gemini API를 호출하여 제목 및 질문 5개를 생성한다.
     *
     * @param prompt 사용자가 입력한 프롬프트
     * @return GeminiResponse 생성된 제목 및 질문 리스트
     * @throws CustomException Gemini API 호출 실패 또는 응답 파싱 실패 시 발생
     */
    @Override
    public GeminiResponse generateQuestions(String prompt) {
        try {
            String requestBody = buildGeminiRequestBody(MULTI_QUESTION_PROMPT, prompt);
            String responseBody = getGeminiResponseBody(requestBody);

            log.error("[Gemini] 응답 본문: {}", responseBody);

            JsonNode root = objectMapper.readTree(responseBody);
            String content = root.path("candidates").get(0)
                                .path("content")
                                .path("parts").get(0)
                                .path("text").asText()
                                // 백틱 제거
                                .replaceAll("```json", "")
                                .replaceAll("```", "")
                                .trim();

            JsonNode jsonNode = objectMapper.readTree(content);
            String title = jsonNode.path("title").asText();
            JsonNode questions = jsonNode.path("questions");

            List<QuestionItem> questionList = new ArrayList<>();
            for (JsonNode q : questions) {
                questionList.add(new QuestionItem(q.asText()));
            }

            return new GeminiResponse(title, questionList);

        } catch (JsonProcessingException e) {
            log.error("[Gemini] 응답 파싱 실패: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.GEMINI_API_RESPONSE_PARSE_ERROR);
        } catch (RestClientException e) {
            log.error("[Gemini] API 통신 실패: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.GEMINI_API_CALL_FAILED);
        }

    }

    /**
     * methodName : generateSingleQuestion
     * description : Gemini API를 호출하여 질문 1개만 재생성한다.
     *
     * @param prompt 사용자가 입력한 프롬프트
     * @return String 재생성된 단일 질문
     * @throws CustomException Gemini API 호출 실패 또는 응답 파싱 실패 시 발생
     */
    @Override
    public String generateSingleQuestion(String prompt) {
        try {
            String requestBody = buildGeminiRequestBody(SINGLE_QUESTION_PROMPT, prompt);
            String responseBody = getGeminiResponseBody(requestBody);

            log.error("[Gemini] 응답 본문: {}", responseBody);

            JsonNode root = objectMapper.readTree(responseBody);
            String content = root.path("candidates").get(0)
                                .path("content")
                                .path("parts").get(0)
                                .path("text").asText()
                                // 백틱 제거
                                .replaceAll("```json", "")
                                .replaceAll("```", "")
                                .trim();

            JsonNode json = objectMapper.readTree(content);
            return json.path("question").asText();

        } catch (JsonProcessingException e) {
            log.error("[Gemini] 단일 응답 파싱 실패: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.GEMINI_API_RESPONSE_PARSE_ERROR);
        } catch (RestClientException e) {
            log.error("[Gemini] 단일 질문 생성 통신 실패: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.GEMINI_API_CALL_FAILED);
        }

    }

    /**
     * methodName : buildGeminiRequestBody
     * description : Gemini API 요청 바디를 구성하는 헬퍼 메서드
     *
     * @param systemPrompt 시스템 메시지 (요청 형식 안내 등)
     * @param userPrompt 사용자 입력 프롬프트
     * @return String Gemini API에 보낼 JSON 요청 문자열
     * @throws CustomException 요청 바디 직렬화 실패 시
     */
    private String buildGeminiRequestBody(String systemPrompt, String userPrompt) {
        try {
            String finalPrompt = systemPrompt + "\n\n" + userPrompt;
            String escapedPrompt = objectMapper.writeValueAsString(finalPrompt);

            return """
            {
              "contents": [
                {
                  "role": "user",
                  "parts": [
                    {
                      "text": %s
                    }
                  ]
                }
              ]
            }
            """.formatted(escapedPrompt);
        } catch (JsonProcessingException e) {
            log.error("[Gemini] 요청 바디 직렬화 실패", e);
            throw new CustomException(ErrorCode.GEMINI_API_REQUEST_ERROR);
        }
    }

    /**
     * methodName : getGeminiResponseBody
     * description : 주어진 요청 바디를 기반으로 Gemini API를 호출하고, 응답 바디를 반환한다.
     *
     * @param requestBody String 요청 본문 (JSON 형식)
     * @return String Gemini API의 응답 바디
     * @throws CustomException 통신 오류 또는 응답 실패 시 예외 발생
     */
    private String getGeminiResponseBody(String requestBody) {
        try {
            // 헤더 및 요청 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-goog-api-key", geminiConfig.getApiKey());

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            // Gemini API 호출
            ResponseEntity<String> response = restTemplate.exchange(
                    geminiConfig.getApiUrl(),
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // Gemini 응답 파싱
            return response.getBody();

        } catch (RestClientException e) {
            log.error("[Gemini] API 호출 실패", e);
            throw new CustomException(ErrorCode.GEMINI_API_CALL_FAILED);
        }

    }

}
