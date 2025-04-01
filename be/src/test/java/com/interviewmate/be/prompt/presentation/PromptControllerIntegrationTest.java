package com.interviewmate.be.prompt.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewmate.be.auth.domain.User;
import com.interviewmate.be.common.security.JwtTokenProvider;
import com.interviewmate.be.infrastructure.persistence.prompt.PromptRepository;
import com.interviewmate.be.infrastructure.persistence.question.QuestionRepository;
import com.interviewmate.be.prompt.domain.Prompt;
import com.interviewmate.be.prompt.dto.PromptListResponse;
import com.interviewmate.be.question.domain.Question;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * packageName    : com.interviewmate.be.prompt.presentation
 * fileName       : RealPromptControllerIntegrationTest
 * author         : eumsoli
 * date           : 2025-03-30
 * description    : PromptController의 실제 HTTP 흐름과 데이터베이스 상호작용을 통합 테스트하는 클래스
 */
@SpringBootTest
@AutoConfigureMockMvc
class PromptControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PromptRepository promptRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private EntityManager entityManager;

    private User testUser;
    private Prompt testPrompt;
    private Question testQuestion;
    private String accessToken;

    private User anotherUser;
    private String anotherToken;

    @BeforeEach
    void setUp() {
        // 테스트 사용자 생성
        testUser = User.builder()
                .email("soli@test.com")
                .name("통합테스트 사용자")
                .providerId("1234567890")
                .provider("google")
                .build();
        entityManager.persist(testUser);
        entityManager.flush();

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", testUser.getId());
        claims.put("email", testUser.getEmail());
        claims.put("name", testUser.getName());
        claims.put("providerId", testUser.getProviderId());
        claims.put("provider", testUser.getProvider());

        // JWT 토큰 생성
        accessToken = jwtTokenProvider.generateAccessToken(claims);

        // 다른 사용자 생성
        anotherUser = User.builder()
                .email("another@test.com")
                .name("다른 사용자")
                .providerId("0987654321")
                .provider("google")
                .build();
        entityManager.persist(anotherUser);
        entityManager.flush();

        Map<String, Object> anotherClaims = new HashMap<>();
        anotherClaims.put("id", anotherUser.getId());
        anotherClaims.put("email", anotherUser.getEmail());
        anotherClaims.put("name", anotherUser.getName());
        anotherClaims.put("providerId", anotherUser.getProviderId());
        anotherClaims.put("provider", anotherUser.getProvider());

        anotherToken = jwtTokenProvider.generateAccessToken(anotherClaims);

        // 테스트 프롬프트 생성
        testPrompt = Prompt.builder()
                .user(testUser)
                .title("테스트 프롬프트 제목")
                .prompt("테스트 프롬프트 내용")
                .build();

        // isActive 필드는 setter가 없고 비즈니스 로직에서만 변경됨 → 테스트 목적상 직접 주입
        ReflectionTestUtils.setField(testPrompt, "isActive", true);
        promptRepository.save(testPrompt);

        // 테스트 질문 생성
        testQuestion = Question.builder()
                .prompt(testPrompt)
                .question("테스트 질문 내용")
                .build();
        ReflectionTestUtils.setField(testQuestion, "isActive", true);
        questionRepository.save(testQuestion);

        entityManager.flush();
    }

    @AfterEach
    void tearDown() {
        entityManager.clear(); // DB 상태 새로 반영 위해 영속성 컨텍스트 초기화
    }

    @Test
    @DisplayName("성공적으로 프롬프트 목록을 조회한다")
    @Transactional
    void getPromptList_WithAuthenticatedUser_ReturnsPromptList() throws Exception {
        // When & Then
        MvcResult result = mockMvc.perform(get("/api/prompts")
                        .requestAttr("user", testUser)
                        .principal(() -> testUser.getId().toString())
                        .header("Authorization", "Bearer " + accessToken)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        // JSON 응답을 PromptListResponse 리스트로 변환
        String responseJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        List<PromptListResponse> responseList = objectMapper.readValue(responseJson,
                objectMapper.getTypeFactory().constructCollectionType(List.class, PromptListResponse.class));

        // 응답 검증
        assertThat(responseList).isNotEmpty();
        assertThat(responseList).hasSize(1);
        assertThat(responseList.get(0).title()).isEqualTo("테스트 프롬프트 제목");
        assertThat(responseList.get(0).prompt()).isEqualTo("테스트 프롬프트 내용");
    }

    @Test
    @DisplayName("성공적으로 프롬프트와 함께 연결된 질문을 비활성화한다")
    @Transactional
    void deactivatePrompt_WithAuthenticatedUser_DeactivatesPromptAndQuestions() throws Exception {
        // When
        mockMvc.perform(delete("/api/prompts/" + testPrompt.getId())
                        .requestAttr("user", testUser)
                        .principal(() -> testUser.getId().toString())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());

        // Then - DB에서 직접 확인
        entityManager.flush();
        entityManager.clear(); // 영속성 컨텍스트 초기화하여 DB에서 새로 조회

        Prompt deactivatedPrompt = promptRepository.findById(testPrompt.getId()).orElseThrow();
        Question deactivatedQuestion = questionRepository.findById(testQuestion.getId()).orElseThrow();

        assertThat(deactivatedPrompt.isActive()).isFalse();
        assertThat(deactivatedQuestion.isActive()).isFalse();
    }

    @Test
    @DisplayName("다른 사용자가 프롬프트를 비활성화하려 하면 403 Forbidden 오류가 발생한다")
    @Transactional
    void deactivatePrompt_WithDifferentUser_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/prompts/" + testPrompt.getId())
                        .requestAttr("user", anotherUser)
                        .principal(() -> anotherUser.getId().toString())
                        .header("Authorization", "Bearer " + anotherToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("해당 프롬프트에 대한 권한이 없습니다."))
                .andDo(print());

        // Then - 프롬프트와 질문이 여전히 활성화 상태인지 확인
        entityManager.clear();

        Prompt prompt = promptRepository.findById(testPrompt.getId()).orElseThrow();
        Question question = questionRepository.findById(testQuestion.getId()).orElseThrow();

        assertThat(prompt.isActive()).isTrue();
        assertThat(question.isActive()).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 프롬프트 ID로 요청하면 404 Not Found 오류가 발생한다")
    @Transactional
    void deactivatePrompt_WithNonExistentId_ReturnsNotFound() throws Exception {
        // Given
        long nonExistentId = 9999L;

        // When & Then
        mockMvc.perform(delete("/api/prompts/" + nonExistentId)
                        .requestAttr("user", testUser)
                        .principal(() -> testUser.getId().toString())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("프롬프트를 찾을 수 없습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("이미 비활성화된 프롬프트를 다시 비활성화하려고 하면 400 Bad Request 오류가 발생한다")
    @Transactional
    void deactivatePrompt_WithAlreadyDeactivatedPrompt_ReturnsBadRequest() throws Exception {
        // Given - 프롬프트 비활성화
        testPrompt.deactivate();
        promptRepository.save(testPrompt);
        entityManager.flush();
        entityManager.clear();

        // When & Then
        mockMvc.perform(delete("/api/prompts/" + testPrompt.getId())
                        .requestAttr("user", testUser)
                        .principal(() -> testUser.getId().toString())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 비활성화된 프롬프트입니다."))
                .andDo(print());
    }

}
