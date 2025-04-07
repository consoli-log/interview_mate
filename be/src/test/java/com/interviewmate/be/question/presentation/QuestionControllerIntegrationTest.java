package com.interviewmate.be.question.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewmate.be.auth.domain.User;
import com.interviewmate.be.common.security.JwtTokenProvider;
import com.interviewmate.be.infrastructure.persistence.prompt.PromptRepository;
import com.interviewmate.be.infrastructure.persistence.question.QuestionRepository;
import com.interviewmate.be.prompt.domain.Prompt;
import com.interviewmate.be.question.domain.Question;
import com.interviewmate.be.question.dto.QuestionGenerateRequest;
import com.interviewmate.be.question.dto.QuestionListResponse;
import com.interviewmate.be.question.dto.QuestionRegenerateRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * packageName    : com.interviewmate.be.question.presentation
 * fileName       : QuestionControllerIntegrationTest
 * author         : eumsoli
 * date           : 2025-04-03
 * description    : QuestionController의 실제 HTTP 흐름과 데이터베이스 상호작용을 통합 테스트하는 클래스
 */
@SpringBootTest
@AutoConfigureMockMvc
class QuestionControllerIntegrationTest {

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

    private User mockUser;
    private Prompt mockPrompt;
    private Question mockQuestion;
    private String accessToken;

    private User anotherUser;
    private String anotherToken;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .email("soli@test.com")
                .name("통합테스트 사용자")
                .providerId("1234567890")
                .provider("google")
                .build();
        entityManager.persist(mockUser);
        entityManager.flush();

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", mockUser.getId());
        claims.put("email", mockUser.getEmail());
        claims.put("name", mockUser.getName());
        claims.put("providerId", mockUser.getProviderId());
        claims.put("provider", mockUser.getProvider());

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

        mockPrompt = Prompt.builder()
                .user(mockUser)
                .title("테스트 프롬프트 제목")
                .prompt("테스트 프롬프트 내용")
                .build();
        ReflectionTestUtils.setField(mockPrompt, "isActive", true);
        promptRepository.save(mockPrompt);

        mockQuestion = Question.builder()
                .prompt(mockPrompt)
                .number(1)
                .question("테스트 질문 내용")
                .build();

        ReflectionTestUtils.setField(mockQuestion, "isActive", true);
        questionRepository.save(mockQuestion);

        entityManager.flush();
    }

    @AfterEach
    void tearDown() {
        entityManager.clear(); // DB 상태 새로 반영 위해 영속성 컨텍스트 초기화
    }


    @Test
    @DisplayName("성공적으로 질문을 생성한다")
    @Transactional
    void generateQuestions_Success() throws Exception {
        // Given
        QuestionGenerateRequest request = new QuestionGenerateRequest("자기소개서를 기반으로 질문 생성");

        // When & Then
        MvcResult result = mockMvc.perform(post("/api/questions/generate")
                        .requestAttr("user", mockUser)
                        .principal(() -> mockUser.getId().toString())
                        .header("Authorization", "Bearer " + accessToken)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.length()").value(5))
                .andReturn();

        // JSON 응답을 Question 리스트로 변환
        String responseJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        List<Question> questionList = objectMapper.readValue(responseJson,
                objectMapper.getTypeFactory().constructCollectionType(List.class, Question.class));

        // 응답 검증
        assertThat(questionList).isNotEmpty();
        assertThat(questionList).hasSize(5);
        assertThat(questionList.get(0).getQuestion()).isNotBlank();
    }

    @Test
    @DisplayName("성공적으로 질문을 비활성화한다")
    @Transactional
    void deactivateQuestion_WithAuthenticatedUser_DeactivatesQuestion() throws Exception {
        // When
        mockMvc.perform(delete("/api/questions/" + mockQuestion.getId())
                        .requestAttr("user", mockUser)
                        .principal(() -> mockUser.getId().toString())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());

        // Then - DB에서 직접 확인
        entityManager.flush();
        entityManager.clear(); // 영속성 컨텍스트 초기화하여 DB에서 새로 조회

        Question deactivatedQuestion = questionRepository.findById(mockQuestion.getId()).orElseThrow();

        assertThat(deactivatedQuestion.isActive()).isFalse();
    }

    @Test
    @DisplayName("다른 사용자가 질문을 비활성화하려 하면 403 Forbidden 오류가 발생한다")
    @Transactional
    void deactivateQuestion_WithDifferentUser_ReturnsForbidden() throws Exception {
        // When
        mockMvc.perform(delete("/api/questions/" + mockQuestion.getId())
                        .requestAttr("user", anotherUser)
                        .principal(() -> anotherUser.getId().toString())
                        .header("Authorization", "Bearer " + anotherToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("해당 질문에 대한 권한이 없습니다."))
                .andDo(print());

        // Then - 질문이 여전히 활성화 상태인지 확인
        entityManager.clear();

        Question question = questionRepository.findById(mockQuestion.getId()).orElseThrow();

        assertThat(question.isActive()).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 질문 ID로 요청하면 404 Not Found 오류가 발생한다")
    @Transactional
    void deactivateQuestion_WithNonExistentId_ReturnsNotFound() throws Exception {
        // Given
        long nonExistentId = 9999L;

        // When & Then
        mockMvc.perform(delete("/api/questions/" + nonExistentId)
                        .requestAttr("user", mockUser)
                        .principal(() -> mockUser.getId().toString())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("질문을 찾을 수 없습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("이미 비활성화된 질문을 다시 비활성화하려고 하면 400 Bad Request 오류가 발생한다")
    @Transactional
    void deactivateQuestion_WithAlreadyDeactivatedQuestion_ReturnsBadRequest() throws Exception {
        // Given - 질문 비활성화
        mockQuestion.deactivate();

        questionRepository.save(mockQuestion);
        entityManager.flush();
        entityManager.clear();

        // When & Then
        mockMvc.perform(delete("/api/questions/" + mockQuestion.getId())
                        .requestAttr("user", mockUser)
                        .principal(() -> mockUser.getId().toString())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 비활성화된 질문입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("성공적으로 질문 목록을 조회한다")
    @Transactional
    void getQuestions_WithAuthenticatedUser_ReturnsQuestionList() throws Exception {
        // When & Then
        MvcResult result = mockMvc.perform(get("/api/questions/prompt/" + mockPrompt.getId())
                        .requestAttr("user", mockUser)
                        .principal(() -> mockUser.getId().toString())
                        .header("Authorization", "Bearer " + accessToken)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        // JSON 응답을 PromptListResponse 리스트로 변환
        String responseJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        QuestionListResponse response = objectMapper.readValue(responseJson, QuestionListResponse.class);

        // 응답 검증
        assertThat(response.questions()).hasSize(1);
        assertThat(response.questions().get(0).question()).isEqualTo("테스트 질문 내용");
    }

    @Test
    @DisplayName("성공적으로 질문을 재생성한다")
    @Transactional
    void regenerateQuestion_Success() throws Exception {
        // Given - 질문 비활성화
        mockQuestion.deactivate();

        questionRepository.save(mockQuestion);
        entityManager.flush();
        entityManager.clear();

        QuestionRegenerateRequest request = new QuestionRegenerateRequest(mockPrompt.getId());

        MvcResult result = mockMvc.perform(post("/api/questions/regenerate")
                        .requestAttr("user", mockUser)
                        .principal(() -> mockUser.getId().toString())
                        .header("Authorization", "Bearer " + accessToken)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.number").value(1))
                .andReturn();

        // JSON 응답을 Question 리스트로 변환
        String responseJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        Question question = objectMapper.readValue(responseJson, Question.class);

        // 응답 검증
        assertThat(question.getQuestion()).isNotBlank();
        assertThat(question.getNumber()).isEqualTo(1);
    }

    @Test
    @DisplayName("비활설화된 질문이 없어서 재생성 할 수 없으면 400 Bad Request를 반환한다")
    @Transactional
    void regenerateQuestion_NoInactiveQuestion_Returns400BadRequest() throws Exception {
        QuestionRegenerateRequest request = new QuestionRegenerateRequest(mockPrompt.getId());

        mockMvc.perform(post("/api/questions/regenerate")
                        .requestAttr("user", mockUser)
                        .principal(() -> mockUser.getId().toString())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("재생성할 수 있는 질문이 없습니다."));
    }

}
