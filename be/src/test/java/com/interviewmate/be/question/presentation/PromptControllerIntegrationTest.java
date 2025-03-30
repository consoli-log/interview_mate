package com.interviewmate.be.question.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewmate.be.auth.domain.User;
import com.interviewmate.be.common.exception.CustomException;
import com.interviewmate.be.common.exception.ErrorCode;
import com.interviewmate.be.common.security.SecurityConfig;
import com.interviewmate.be.question.application.PromptService;
import com.interviewmate.be.question.dto.PromptListResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * packageName    : com.interviewmate.be.question.presentation
 * fileName       : PromptControllerIntegrationTest
 * author         : eumsoli
 * date           : 2025-03-30
 * description    : PromptController의 실제 HTTP 흐름을 통합 테스트하는 클래스
 */
@WebMvcTest(PromptController.class)
@Import(SecurityConfig.class) // SecurityConfig를 테스트 컨텍스트에 포함
class PromptControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PromptService promptService;

    private User mockUser;
    private List<PromptListResponse> mockPromptList;
    private LocalDateTime now;

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

    @Test
    @DisplayName("인증된 사용자는 프롬프트 목록을 성공적으로 조회한다")
    @WithMockUser
    void getPromptList_AuthenticatedUser_ReturnsOkWithPromptList() throws Exception {
        // Given
        given(promptService.getPromptList(any(User.class))).willReturn(mockPromptList);

        // When & Then
        mockMvc.perform(get("/api/prompts")
                        .with(request -> {
                            request.setAttribute("user", mockUser);
                            return request;
                        })
                        .principal(() -> "user")
                        .contentType(MediaType.APPLICATION_JSON))
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
    @WithMockUser
    void getPromptList_AuthenticatedUserWithNoPrompts_ReturnsOkWithEmptyList() throws Exception {
        // Given
        given(promptService.getPromptList(any(User.class))).willReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/prompts")
                        .with(request -> {
                            request.setAttribute("user", mockUser);
                            return request;
                        })
                        .principal(() -> "user")
                        .contentType(MediaType.APPLICATION_JSON))
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
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(promptService, never()).getPromptList(any(User.class));
    }

    @Test
    @DisplayName("인증된 사용자는 프롬프트를 성공적으로 비활성화한다")
    @WithMockUser
    void deactivatePrompt_AuthenticatedUser_Returns204NoContent() throws Exception {
        // Given
        long promptId = 1L;
        doNothing().when(promptService).deactivatePrompt(eq(promptId), any(User.class));

        // When & Then
        mockMvc.perform(delete("/api/prompts/{promptId}", promptId)
                        .with(request -> {
                            request.setAttribute("user", mockUser);
                            return request;
                        })
                        .principal(() -> "user")
                        .contentType(MediaType.APPLICATION_JSON))
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
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(promptService, never()).deactivatePrompt(anyLong(), any(User.class));
    }

    @Test
    @DisplayName("존재하지 않는 프롬프트 ID로 요청하면 404 Not Found를 반환한다")
    @WithMockUser
    void deactivatePrompt_NonexistentPromptId_Returns404NotFound() throws Exception {
        // Given
        long nonExistentPromptId = 999L;
        doThrow(new CustomException(ErrorCode.PROMPT_NOT_FOUND))
                .when(promptService).deactivatePrompt(eq(nonExistentPromptId), any(User.class));

        // When & Then
        mockMvc.perform(delete("/api/prompts/{promptId}", nonExistentPromptId)
                        .with(request -> {
                            request.setAttribute("user", mockUser);
                            return request;
                        })
                        .principal(() -> "user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(promptService, times(1)).deactivatePrompt(eq(nonExistentPromptId), any(User.class));
    }

    @Test
    @DisplayName("다른 사용자의 프롬프트를 삭제하려고 하면 403 Forbidden을 반환한다")
    @WithMockUser
    void deactivatePrompt_PromptOwnedByAnotherUser_Returns403Forbidden() throws Exception {
        // Given
        long promptIdOwnedByAnotherUser = 2L;
        doThrow(new CustomException(ErrorCode.PROMPT_NOT_OWNED))
                .when(promptService).deactivatePrompt(eq(promptIdOwnedByAnotherUser), any(User.class));

        // When & Then
        mockMvc.perform(delete("/api/prompts/{promptId}", promptIdOwnedByAnotherUser)
                        .with(request -> {
                            request.setAttribute("user", mockUser);
                            return request;
                        })
                        .principal(() -> "user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(promptService, times(1)).deactivatePrompt(eq(promptIdOwnedByAnotherUser), any(User.class));
    }

    @Test
    @DisplayName("이미 비활성화된 프롬프트를 삭제하려고 하면 400 Bad Request를 반환한다")
    @WithMockUser
    void deactivatePrompt_AlreadyDeactivatedPrompt_Returns400BadRequest() throws Exception {
        // Given
        long deactivatedPromptId = 3L;
        doThrow(new CustomException(ErrorCode.PROMPT_ALREADY_DEACTIVATED))
                .when(promptService).deactivatePrompt(eq(deactivatedPromptId), any(User.class));

        // When & Then
        mockMvc.perform(delete("/api/prompts/{promptId}", deactivatedPromptId)
                        .with(request -> {
                            request.setAttribute("user", mockUser);
                            return request;
                        })
                        .principal(() -> "user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(promptService, times(1)).deactivatePrompt(eq(deactivatedPromptId), any(User.class));
    }

}
