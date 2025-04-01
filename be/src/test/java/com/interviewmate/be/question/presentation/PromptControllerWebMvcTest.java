package com.interviewmate.be.question.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewmate.be.auth.domain.User;
import com.interviewmate.be.common.exception.CustomException;
import com.interviewmate.be.common.exception.ErrorCode;
import com.interviewmate.be.question.application.PromptService;
import com.interviewmate.be.question.dto.PromptListResponse;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * packageName    : com.interviewmate.be.question.presentation
 * fileName       : PromptControllerWebMvcTest
 * author         : eumsoli
 * date           : 2025-03-30
 * description    : PromptController의 웹 계층의 동작을 슬라이스 테스트하는 클래스
 */
@WebMvcTest(PromptController.class)
@AutoConfigureMockMvc(addFilters = false)
class PromptControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PromptService promptService;

    private User mockUser;
    private List<PromptListResponse> mockPromptList;
    private LocalDateTime now;

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
        // Mock 시간 설정
        now = LocalDateTime.now();

        // Mock 사용자 생성
        mockUser = User.builder()
                .email("soli@test.com")
                .name("사용자")
                .build();

        ReflectionTestUtils.setField(mockUser, "id", 1L);

        // Mock 프롬프트 응답 생성
        mockPromptList = Arrays.asList(
                new PromptListResponse(1L, "프롬프트 1", "프롬프트 내용 1", now),
                new PromptListResponse(2L, "프롬프트 2", "프롬프트 내용 2", now.plusHours(1))
        );

    }

    @Nested
    @DisplayName("프롬프트 목록 조회 API 테스트")
    class GetPromptListTest {

        @Test
        @DisplayName("인증된 사용자는 프롬프트 목록을 성공적으로 조회한다")
        @WithMockUser
        void getPromptList_AuthenticatedUser_ReturnsOkWithPromptList() throws Exception {
            // Given
            given(promptService.getPromptList(any(User.class))).willReturn(mockPromptList);

            // When & Then
            mockMvc.perform(get("/api/prompts")
                            .with(mockPrincipal(mockUser))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].promptId").value(1L))
                    .andExpect(jsonPath("$[0].title").value("프롬프트 1"))
                    .andExpect(jsonPath("$[0].prompt").value("프롬프트 내용 1"))
                    .andExpect(jsonPath("$[1].promptId").value(2L));

            verify(promptService, times(1)).getPromptList(any(User.class));
        }

        @Test
        @DisplayName("인증된 사용자의 프롬프트가 없는 경우 빈 목록을 반환한다")
        @WithMockUser("soli")
        void getPromptList_AuthenticatedUserWithNoPrompts_ReturnsOkWithEmptyList() throws Exception {
            // Given
            given(promptService.getPromptList(any(User.class))).willReturn(Collections.emptyList());

            // When & Then
            mockMvc.perform(get("/api/prompts")
                            .with(mockPrincipal(mockUser))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(promptService, times(1)).getPromptList(any(User.class));
        }

        @Test
        @DisplayName("인증되지 않은 사용자는 401 Unauthorized를 반환한다")
        void getPromptList_UnauthenticatedUser_Returns401Unauthorized() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/prompts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());

            verify(promptService, never()).getPromptList(any(User.class));
        }

    }

    @Nested
    @DisplayName("프롬프트 삭제 API 테스트")
    class DeactivatePromptTest {

        @Test
        @DisplayName("인증된 사용자는 프롬프트를 성공적으로 비활성화한다")
        @WithMockUser("soli")
        void deactivatePrompt_AuthenticatedUser_Returns204NoContent() throws Exception {
            // Given
            long promptId = 1L;
            doNothing().when(promptService).deactivatePrompt(eq(promptId), any(User.class));

            // When & Then
            mockMvc.perform(delete("/api/prompts/{promptId}", promptId)
                            .with(mockPrincipal(mockUser))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNoContent());

            verify(promptService, times(1)).deactivatePrompt(eq(promptId), any(User.class));
        }

        @Test
        @DisplayName("인증되지 않은 사용자가 프롬프트 비활성화를 시도하면 401 Unauthorized를 반환한다")
        void deactivatePrompt_UnauthenticatedUser_Returns401Unauthorized() throws Exception {
            // Given
            long promptId = 1L;

            // When & Then
            mockMvc.perform(delete("/api/prompts/{promptId}", promptId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());

            verify(promptService, never()).deactivatePrompt(anyLong(), any(User.class));
        }

        @Test
        @DisplayName("존재하지 않는 프롬프트 ID로 요청하면 404 Not Found를 반환한다")
        @WithMockUser("soli")
        void deactivatePrompt_NonexistentPromptId_Returns404NotFound() throws Exception {
            // Given
            long nonExistentPromptId = 999L;
            doThrow(new CustomException(ErrorCode.PROMPT_NOT_FOUND))
                    .when(promptService).deactivatePrompt(eq(nonExistentPromptId), any(User.class));

            // When & Then
            mockMvc.perform(delete("/api/prompts/{promptId}", nonExistentPromptId)
                            .with(mockPrincipal(mockUser))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound());

            verify(promptService, times(1)).deactivatePrompt(eq(nonExistentPromptId), any(User.class));
        }

        @Test
        @DisplayName("다른 사용자의 프롬프트를 삭제하려고 하면 403 Forbidden을 반환한다")
        @WithMockUser("soli")
        void deactivatePrompt_PromptOwnedByAnotherUser_Returns403Forbidden() throws Exception {
            // Given
            long promptIdOwnedByAnotherUser = 2L;
            doThrow(new CustomException(ErrorCode.PROMPT_ACCESS_DENIED))
                    .when(promptService).deactivatePrompt(eq(promptIdOwnedByAnotherUser), any(User.class));

            // When & Then
            mockMvc.perform(delete("/api/prompts/{promptId}", promptIdOwnedByAnotherUser)
                            .with(mockPrincipal(mockUser))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isForbidden());

            verify(promptService, times(1)).deactivatePrompt(eq(promptIdOwnedByAnotherUser), any(User.class));
        }

        @Test
        @DisplayName("이미 비활성화된 프롬프트를 삭제하려고 하면 400 Bad Request를 반환한다")
        @WithMockUser("soli")
        void deactivatePrompt_AlreadyDeactivatedPrompt_Returns400BadRequest() throws Exception {
            // Given
            long deactivatedPromptId = 3L;
            doThrow(new CustomException(ErrorCode.PROMPT_ALREADY_DEACTIVATED))
                    .when(promptService).deactivatePrompt(eq(deactivatedPromptId), any(User.class));

            // When & Then
            mockMvc.perform(delete("/api/prompts/{promptId}", deactivatedPromptId)
                            .with(mockPrincipal(mockUser))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(promptService, times(1)).deactivatePrompt(eq(deactivatedPromptId), any(User.class));
        }
    }

}
