package com.interviewmate.be.question.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewmate.be.auth.domain.User;
import com.interviewmate.be.common.exception.CustomException;
import com.interviewmate.be.common.exception.ErrorCode;
import com.interviewmate.be.question.application.QuestionService;
import com.interviewmate.be.question.dto.QuestionGenerateRequest;
import com.interviewmate.be.question.dto.QuestionListResponse;
import com.interviewmate.be.question.dto.QuestionRegenerateRequest;
import com.interviewmate.be.question.dto.QuestionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * packageName    : com.interviewmate.be.question.presentation
 * fileName       : QuestionControllerWebMvcTest
 * author         : eumsoli
 * date           : 2025-04-01
 * description    : QuestionController의 API를 WebMvcTest 환경에서 검증하는 테스트 클래스
 */
@WebMvcTest(QuestionController.class)
@AutoConfigureMockMvc(addFilters = false)
class QuestionControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private QuestionService questionService;

    private User mockUser;
    private List<QuestionResponse> mockQuestionList;

    private RequestPostProcessor mockPrincipal(User user) {
        return request -> {
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(user, null, List.of());
            SecurityContextHolder.getContext().setAuthentication(auth);
            return request;
        };
    }

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .email("soli@test.com")
                .name("사용자")
                .provider("google")
                .providerId("1234567890")
                .build();

        ReflectionTestUtils.setField(mockUser, "id", 1L);

        mockQuestionList = List.of(
                new QuestionResponse(1, "질문1"),
                new QuestionResponse(2, "질문2")
        );
    }

    @Nested
    @DisplayName("질문 생성 API 테스트")
    class GenerateQuestionsTest {

        @Test
        @DisplayName("인증된 사용자는 질문 리스트를 성공적으로 생성한다")
        void generateQuestions_Success() throws Exception {
            // Given
            QuestionGenerateRequest request = new QuestionGenerateRequest("프롬프트 내용");
            given(questionService.generateQuestions(any(), any())).willReturn(mockQuestionList);

            // When & Then
            mockMvc.perform(post("/api/questions/generate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .with(mockPrincipal(mockUser)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].number").value(1));
        }

    }

    @Nested
    @DisplayName("질문 삭제 API 테스트")
    class DeactivateQuestionTest {

        @Test
        @DisplayName("인증된 사용자는 본인 질문을 성공적으로 비활성화한다")
        void deactivateQuestion_AuthenticatedUser_Returns204NoContent() throws Exception {
            // Given
            long questionId = 1L;
            doNothing().when(questionService).deactivateQuestion(eq(questionId), any(User.class));

            // When & Then
            mockMvc.perform(delete("/api/questions/{questionId}", questionId)
                            .with(mockPrincipal(mockUser))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNoContent());

            verify(questionService, times(1)).deactivateQuestion(eq(questionId), any(User.class));
        }

        @Test
        @DisplayName("인증되지 않은 사용자가 질문 비활성화를 시도하면 401 Unauthorized를 반환한다")
        void deactivateQuestion_UnauthenticatedUser_Returns401Unauthorized() throws Exception {
            // Given
            long questionId = 1L;

            // When & Then
            mockMvc.perform(delete("/api/questions/{questionId}", questionId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());

            verify(questionService, never()).deactivateQuestion(anyLong(), any(User.class));
        }

        @Test
        @DisplayName("존재하지 않는 질문 ID로 요청하면 404 Not Found를 반환한다")
        void deactivateQuestion_NonexistentQuestionId_Returns404NotFound() throws Exception {
            // Given
            long nonExistentQuestionId = 999L;
            doThrow(new CustomException(ErrorCode.QUESTION_NOT_FOUND))
                    .when(questionService).deactivateQuestion(eq(nonExistentQuestionId), any(User.class));

            // When & Then
            mockMvc.perform(delete("/api/questions/{questionId}", nonExistentQuestionId)
                            .with(mockPrincipal(mockUser))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound());

            verify(questionService, times(1)).deactivateQuestion(eq(nonExistentQuestionId), any(User.class));
        }

        @Test
        @DisplayName("다른 사용자의 질문을 삭제하려고 하면 403 Forbidden을 반환한다")
        void deactivateQuestion_QuestionOwnedByAnotherUser_Returns403Forbidden() throws Exception {
            // Given
            long questionIdOwnedByAnotherUser = 2L;
            doThrow(new CustomException(ErrorCode.QUESTION_ACCESS_DENIED))
                    .when(questionService).deactivateQuestion(eq(questionIdOwnedByAnotherUser), any(User.class));

            // When & Then
            mockMvc.perform(delete("/api/questions/{questionId}", questionIdOwnedByAnotherUser)
                            .with(mockPrincipal(mockUser))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isForbidden());

            verify(questionService, times(1)).deactivateQuestion(eq(questionIdOwnedByAnotherUser), any(User.class));
        }

        @Test
        @DisplayName("이미 비활성화된 프롬프트를 삭제하려고 하면 400 Bad Request를 반환한다")
        void deactivateQuestion_AlreadyDeactivatedQuestion_Returns400BadRequest() throws Exception {
            // Given
            long deactivatedQuestionId = 3L;
            doThrow(new CustomException(ErrorCode.QUESTION_ALREADY_DEACTIVATED))
                    .when(questionService).deactivateQuestion(eq(deactivatedQuestionId), any(User.class));

            // When & Then
            mockMvc.perform(delete("/api/questions/{questionId}", deactivatedQuestionId)
                            .with(mockPrincipal(mockUser))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(questionService, times(1)).deactivateQuestion(eq(deactivatedQuestionId), any(User.class));
        }
    }

    @Nested
    @DisplayName("질문 재생성 API 테스트")
    class RegenerateQuestionTest {

        @Test
        @DisplayName("인증된 사용자는 비활성화 된 질문이 있을 경우 새 질문을 성공적으로 재생성한다")
        void regenerateQuestion_Success() throws Exception {
            // Given
            QuestionRegenerateRequest request = new QuestionRegenerateRequest(1L);
            QuestionResponse response = new QuestionResponse(3, "재생성된 질문");

            given(questionService.regenerateQuestion(any(), any())).willReturn(response);

            // When & Then
            mockMvc.perform(post("/api/questions/regenerate")
                            .with(mockPrincipal(mockUser))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.number").value(3))
                    .andExpect(jsonPath("$.question").value("재생성된 질문"));
        }

        @Test
        @DisplayName("인증되지 않은 사용자는 401 Unauthorized를 반환한다")
        void regenerateQuestion_UnauthenticatedUser_Returns401Unauthorized() throws Exception {
            // Given
            QuestionRegenerateRequest request = new QuestionRegenerateRequest(1L);

            // When & Then
            mockMvc.perform(post("/api/questions/regenerate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());

            verify(questionService, never()).regenerateQuestion(eq(request), any(User.class));
        }

        @Test
        @DisplayName("비활설화된 질문이 없어서 재생성 할 수 없으면 400 Bad Request를 반환한다")
        void regenerateQuestion_NoInactiveQuestion_Returns400BadRequest() throws Exception {
            // Given
            given(questionService.regenerateQuestion(any(), any()))
                    .willThrow(new CustomException(ErrorCode.QUESTION_REGENERATION_NOT_ALLOWED));

            // When & Then
            mockMvc.perform(post("/api/questions/regenerate")
                            .with(mockPrincipal(mockUser))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new QuestionRegenerateRequest(1L))))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(ErrorCode.QUESTION_REGENERATION_NOT_ALLOWED.getMessage()));
        }
    }

    @Nested
    @DisplayName("질문 목록 조회 API 테스트")
    class GetActiveQuestionsTest {

        @Test
        @DisplayName("인증된 사용자는 활성화된 질문 목록을 성공적으로 조회한다")
        void getQuestions_AuthenticatedUser_ReturnsOkWithQuestions() throws Exception {
            // Given
            long promptId = 1L;
            given(questionService.getActiveQuestions(eq(promptId), any(User.class)))
                    .willReturn(new QuestionListResponse(mockQuestionList));

            // When & Then
            mockMvc.perform(get("/api/questions/prompt/{promptId}", promptId)
                            .with(mockPrincipal(mockUser))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(jsonPath("$.questions.length()").value(2));
        }

        @Test
        @DisplayName("인증되지 않은 사용자는 401 Unauthorized를 반환한다")
        void getQuestions_UnauthenticatedUser_Returns401Unauthorized() throws Exception {
            // Given
            long promptId = 1L;

            // When & Then
            mockMvc.perform(get("/api/questions/prompt/{promptId}", promptId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());

            verify(questionService, never()).getActiveQuestions(anyLong(), any(User.class));
        }

    }

}