package com.interviewmate.be.prompt.presentation;

import com.interviewmate.be.auth.domain.User;
import com.interviewmate.be.common.exception.CustomException;
import com.interviewmate.be.common.exception.ErrorCode;
import com.interviewmate.be.prompt.application.PromptService;
import com.interviewmate.be.prompt.dto.PromptListResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

/**
 * packageName    : com.interviewmate.be.prompt.presentation
 * fileName       : PromptControllerTest
 * author         : eumsoli
 * date           : 2025-03-30
 * description    : PromptController의 API 동작을 단위 테스트하는 클래스
 */
@ExtendWith(MockitoExtension.class)
class PromptControllerTest {

    @Mock
    private PromptService promptService;

    @InjectMocks
    private PromptController promptController;

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
                .name("테스트 사용자")
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
        void getPromptList_AuthenticatedUser_ReturnsPromptList() {
            // Given
            given(promptService.getPromptList(mockUser)).willReturn(mockPromptList);

            // When
            ResponseEntity<List<PromptListResponse>> response = promptController.getPromptList(mockUser);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(mockPromptList);
            assertThat(response.getBody().size()).isEqualTo(2);
            assertThat(response.getBody().get(0).promptId()).isEqualTo(1L);
            assertThat(response.getBody().get(0).title()).isEqualTo("프롬프트 1");
            assertThat(response.getBody().get(0).prompt()).isEqualTo("프롬프트 내용 1");
            assertThat(response.getBody().get(1).promptId()).isEqualTo(2L);

            verify(promptService, times(1)).getPromptList(mockUser);
        }

        @Test
        @DisplayName("인증된 사용자가 프롬프트가 없는 경우 빈 목록을 반환한다")
        void getPromptList_AuthenticatedUserWithNoPrompts_ReturnsEmptyList() {
            // Given
            given(promptService.getPromptList(mockUser)).willReturn(Collections.emptyList());

            // When
            ResponseEntity<List<PromptListResponse>> response = promptController.getPromptList(mockUser);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEmpty();
            verify(promptService, times(1)).getPromptList(mockUser);
        }

        @Test
        @DisplayName("인증되지 않은 사용자는 예외가 발생한다")
        void getPromptList_UnauthenticatedUser_ThrowsLoginRequiredException() {
            // Given
            User nullUser = null;

            // When & Then
            assertThatThrownBy(() -> promptController.getPromptList(nullUser))
                    .isInstanceOf(CustomException.class)
                    .satisfies(ex -> {
                        CustomException customEx = (CustomException) ex;
                        assertThat(customEx.getMessage()).isEqualTo(ErrorCode.LOGIN_REQUIRED.getMessage());
                        assertThat(customEx.getHttpStatus()).isEqualTo(ErrorCode.LOGIN_REQUIRED.getHttpStatus());
                    });

            verify(promptService, never()).getPromptList(any());
        }
    }

    @Nested
    @DisplayName("프롬프트 삭제 API 테스트")
    class DeactivatePromptTest {

        @Test
        @DisplayName("인증된 사용자는 프롬프트를 성공적으로 비활성화한다")
        void deactivatePrompt_AuthenticatedUser_SuccessfullyDeactivatesPrompt() {
            // Given
            long promptId = 1L;
            doNothing().when(promptService).deactivatePrompt(eq(promptId), eq(mockUser));

            // When
            ResponseEntity<Void> response = promptController.deactivatePrompt(promptId, mockUser);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(promptService, times(1)).deactivatePrompt(promptId, mockUser);
        }

        @Test
        @DisplayName("인증되지 않은 사용자는 예외가 발생한다")
        void deactivatePrompt_UnauthenticatedUser_ThrowsLoginRequiredException() {
            // Given
            long promptId = 1L;
            User nullUser = null;

            // When & Then
            assertThatThrownBy(() -> promptController.deactivatePrompt(promptId, nullUser))
                    .isInstanceOf(CustomException.class)
                    .satisfies(ex -> {
                        CustomException customEx = (CustomException) ex;
                        assertThat(customEx.getMessage()).isEqualTo(ErrorCode.LOGIN_REQUIRED.getMessage());
                        assertThat(customEx.getHttpStatus()).isEqualTo(ErrorCode.LOGIN_REQUIRED.getHttpStatus());
                    });

            verify(promptService, never()).deactivatePrompt(anyLong(), any());
        }

        @Test
        @DisplayName("존재하지 않는 프롬프트 ID로 요청하면 예외가 발생한다")
        void deactivatePrompt_NonexistentPromptId_ServiceThrowsException() {
            // Given
            long nonExistentPromptId = 999L;
            doThrow(new CustomException(ErrorCode.PROMPT_NOT_FOUND))
                    .when(promptService).deactivatePrompt(eq(nonExistentPromptId), eq(mockUser));

            // When & Then
            assertThatThrownBy(() -> promptController.deactivatePrompt(nonExistentPromptId, mockUser))
                    .isInstanceOf(CustomException.class)
                    .satisfies(ex -> {
                        CustomException customEx = (CustomException) ex;
                        assertThat(customEx.getMessage()).isEqualTo(ErrorCode.PROMPT_NOT_FOUND.getMessage());
                        assertThat(customEx.getHttpStatus()).isEqualTo(ErrorCode.PROMPT_NOT_FOUND.getHttpStatus());
                    });

            verify(promptService, times(1)).deactivatePrompt(nonExistentPromptId, mockUser);
        }

        @Test
        @DisplayName("다른 사용자의 프롬프트를 삭제하려고 하면 예외가 발생한다")
        void deactivatePrompt_PromptOwnedByAnotherUser_ServiceThrowsException() {
            // Given
            long promptIdOwnedByAnotherUser = 2L;
            doThrow(new CustomException(ErrorCode.PROMPT_ACCESS_DENIED))
                    .when(promptService).deactivatePrompt(eq(promptIdOwnedByAnotherUser), eq(mockUser));

            // When & Then
            assertThatThrownBy(() -> promptController.deactivatePrompt(promptIdOwnedByAnotherUser, mockUser))
                    .isInstanceOf(CustomException.class)
                    .satisfies(ex -> {
                        CustomException customEx = (CustomException) ex;
                        assertThat(customEx.getMessage()).isEqualTo(ErrorCode.PROMPT_ACCESS_DENIED.getMessage());
                        assertThat(customEx.getHttpStatus()).isEqualTo(ErrorCode.PROMPT_ACCESS_DENIED.getHttpStatus());
                    });

            verify(promptService, times(1)).deactivatePrompt(promptIdOwnedByAnotherUser, mockUser);
        }

        @Test
        @DisplayName("이미 비활성화된 프롬프트를 삭제하려고 하면 예외가 발생한다")
        void deactivatePrompt_AlreadyDeactivatedPrompt_ServiceThrowsException() {
            // Given
            long deactivatedPromptId = 3L;
            doThrow(new CustomException(ErrorCode.PROMPT_ALREADY_DEACTIVATED))
                    .when(promptService).deactivatePrompt(eq(deactivatedPromptId), eq(mockUser));

            // When & Then
            assertThatThrownBy(() -> promptController.deactivatePrompt(deactivatedPromptId, mockUser))
                    .isInstanceOf(CustomException.class)
                    .satisfies(ex -> {
                        CustomException customEx = (CustomException) ex;
                        assertThat(customEx.getMessage()).isEqualTo(ErrorCode.PROMPT_ALREADY_DEACTIVATED.getMessage());
                        assertThat(customEx.getHttpStatus()).isEqualTo(ErrorCode.PROMPT_ALREADY_DEACTIVATED.getHttpStatus());
                    });

            verify(promptService, times(1)).deactivatePrompt(deactivatedPromptId, mockUser);
        }
    }

}