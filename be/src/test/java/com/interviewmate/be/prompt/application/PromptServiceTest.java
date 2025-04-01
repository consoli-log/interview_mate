package com.interviewmate.be.prompt.application;

import com.interviewmate.be.auth.domain.User;
import com.interviewmate.be.common.exception.CustomException;
import com.interviewmate.be.common.exception.ErrorCode;
import com.interviewmate.be.infrastructure.persistence.prompt.PromptRepository;
import com.interviewmate.be.infrastructure.persistence.question.QuestionRepository;
import com.interviewmate.be.prompt.domain.Prompt;
import com.interviewmate.be.prompt.dto.PromptListResponse;
import com.interviewmate.be.question.domain.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * packageName    : com.interviewmate.be.prompt.application
 * fileName       : PromptServiceTest
 * author         : eumsoli
 * date           : 2025-03-28
 * description    : PromptService의 비즈니스 로직을 테스트하는 클래스
 */
@ExtendWith(MockitoExtension.class)
class PromptServiceTest {

    @Mock
    private PromptRepository promptRepository;

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private PromptService promptService;

    private User mockUser;
    private User anotherUser;

    private Prompt mockPrompt1;
    private Prompt mockPrompt2;

    private List<Prompt> mockPrompts;

    private Question mockQuestion1;
    private Question mockQuestion2;

    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        // 현재 시간 설정
        now = LocalDateTime.now();

        // Mock 사용자 생성
        mockUser = User.builder()
                .email("soli@test.com")
                .name("사용자")
                .build();

        ReflectionTestUtils.setField(mockUser, "id", 1L);

        anotherUser = User.builder()
                .email("another@test.com")
                .name("다른 사용자")
                .build();

        ReflectionTestUtils.setField(anotherUser, "id", 2L);

        // Mock 프롬프트 생성
        mockPrompt1 = Prompt.builder()
                .user(mockUser)
                .title("프롬프트 1")
                .prompt("프롬프트 내용 1")
                .build();

        ReflectionTestUtils.setField(mockPrompt1, "id", 1L);
        ReflectionTestUtils.setField(mockPrompt1, "isActive", true);
        ReflectionTestUtils.setField(mockPrompt1, "createdAt", now);

        mockPrompt2 = Prompt.builder()
                .user(mockUser)
                .title("프롬프트 2")
                .prompt("프롬프트 내용 2")
                .build();

        ReflectionTestUtils.setField(mockPrompt2, "id", 2L);
        ReflectionTestUtils.setField(mockPrompt2, "isActive", true);
        ReflectionTestUtils.setField(mockPrompt2, "createdAt", now.plusHours(1));

        mockPrompts = Arrays.asList(mockPrompt1, mockPrompt2);

        // Mock 질문 생성
        mockQuestion1 = Question.builder()
                .prompt(mockPrompt1)
                .question("질문 1")
                .build();

        ReflectionTestUtils.setField(mockQuestion1, "id", 1L);
        ReflectionTestUtils.setField(mockQuestion1, "isActive", true);

        mockQuestion2 = Question.builder()
                .prompt(mockPrompt1)
                .question("질문 2")
                .build();

        ReflectionTestUtils.setField(mockQuestion2, "id", 2L);
        ReflectionTestUtils.setField(mockQuestion2, "isActive", true);
    }

    @Nested
    @DisplayName("프롬프트 목록 조회 서비스 테스트")
    class GetPromptListTest {

        @Test
        @DisplayName("사용자의 활성화된 프롬프트 목록을 성공적으로 조회한다")
        void getPromptList_ReturnsActivePromptsList() {
            // Given
            given(promptRepository.findAllByUserAndIsActiveTrueOrderByCreatedAtDesc(mockUser))
                    .willReturn(mockPrompts);

            // When
            List<PromptListResponse> result = promptService.getPromptList(mockUser);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).promptId()).isEqualTo(mockPrompt1.getId());
            assertThat(result.get(0).title()).isEqualTo(mockPrompt1.getTitle());
            assertThat(result.get(0).prompt()).isEqualTo(mockPrompt1.getPrompt());
            assertThat(result.get(0).createdAt()).isEqualTo(mockPrompt1.getCreatedAt());
            assertThat(result.get(1).promptId()).isEqualTo(mockPrompt2.getId());

            verify(promptRepository, times(1))
                    .findAllByUserAndIsActiveTrueOrderByCreatedAtDesc(mockUser);
        }

        @Test
        @DisplayName("사용자의 활성화된 프롬프트가 없는 경우 빈 목록을 반환한다")
        void getPromptList_NoActivePrompts_ReturnsEmptyList() {
            // Given
            given(promptRepository.findAllByUserAndIsActiveTrueOrderByCreatedAtDesc(mockUser))
                    .willReturn(Collections.emptyList());

            // When
            List<PromptListResponse> result = promptService.getPromptList(mockUser);

            // Then
            assertThat(result).isEmpty();
            verify(promptRepository, times(1))
                    .findAllByUserAndIsActiveTrueOrderByCreatedAtDesc(mockUser);
        }

    }

    @Nested
    @DisplayName("프롬프트 비활성화 서비스 테스트")
    class DeactivatePromptTest {

        @Test
        @DisplayName("프롬프트를 성공적으로 비활성화하고 연결된 질문도 비활성화한다")
        void deactivatePrompt_SuccessfullyDeactivatesPromptAndQuestions() {
            // Given
            given(promptRepository.findById(eq(1L)))
                    .willReturn(Optional.of(mockPrompt1));
            given(questionRepository.findAllByPromptAndIsActiveTrue(mockPrompt1))
                    .willReturn(Arrays.asList(mockQuestion1, mockQuestion2));

            // When
            promptService.deactivatePrompt(1L, mockUser);

            // Then
            assertThat(mockPrompt1.isActive()).isFalse();
            assertThat(mockQuestion1.isActive()).isFalse();
            assertThat(mockQuestion2.isActive()).isFalse();
            verify(promptRepository, times(1)).findById(1L);
            verify(questionRepository, times(1)).findAllByPromptAndIsActiveTrue(mockPrompt1);
        }

        @Test
        @DisplayName("존재하지 않는 프롬프트 ID로 요청하면 예외가 발생한다")
        void deactivatePrompt_NonexistentPromptId_ThrowsException() {
            // Given
            given(promptRepository.findById(eq(999L))).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> promptService.deactivatePrompt(999L, mockUser))
                    .isInstanceOf(CustomException.class)
                    .satisfies(ex -> {
                        CustomException customEx = (CustomException) ex;
                        assertThat(customEx.getMessage()).isEqualTo(ErrorCode.PROMPT_NOT_FOUND.getMessage());
                        assertThat(customEx.getHttpStatus()).isEqualTo(ErrorCode.PROMPT_NOT_FOUND.getHttpStatus());
                    });

            verify(promptRepository, times(1)).findById(999L);
            verify(questionRepository, never()).findAllByPromptAndIsActiveTrue(any(Prompt.class));
        }

        @Test
        @DisplayName("다른 사용자의 프롬프트를 삭제하려고 하면 예외가 발생한다")
        void deactivatePrompt_PromptOwnedByAnotherUser_ThrowsException() {
            // Given
            Prompt promptOwnedByAnotherUser = Prompt.builder()
                    .user(anotherUser)
                    .title("다른 사용자의 프롬프트")
                    .prompt("다른 사용자의 프롬프트 내용")
                    .build();
            ReflectionTestUtils.setField(promptOwnedByAnotherUser, "id", 3L);
            ReflectionTestUtils.setField(promptOwnedByAnotherUser, "isActive", true);

            given(promptRepository.findById(eq(3L)))
                    .willReturn(Optional.of(promptOwnedByAnotherUser));

            // When & Then
            assertThatThrownBy(() -> promptService.deactivatePrompt(3L, mockUser))
                    .isInstanceOf(CustomException.class)
                    .satisfies(ex -> {
                        CustomException customEx = (CustomException) ex;
                        assertThat(customEx.getMessage()).isEqualTo(ErrorCode.PROMPT_ACCESS_DENIED.getMessage());
                        assertThat(customEx.getHttpStatus()).isEqualTo(ErrorCode.PROMPT_ACCESS_DENIED.getHttpStatus());
                    });

            verify(promptRepository, times(1)).findById(3L);
            verify(questionRepository, never()).findAllByPromptAndIsActiveTrue(any(Prompt.class));
        }

        @Test
        @DisplayName("이미 비활성화된 프롬프트를 삭제하려고 하면 예외가 발생한다")
        void deactivatePrompt_AlreadyDeactivatedPrompt_ThrowsException() {
            // Given
            Prompt deactivatedPrompt = Prompt.builder()
                    .user(mockUser)
                    .title("비활성화된 프롬프트")
                    .prompt("비활성화된 프롬프트 내용")
                    .build();
            ReflectionTestUtils.setField(deactivatedPrompt, "id", 4L);
            ReflectionTestUtils.setField(deactivatedPrompt, "isActive", false);

            given(promptRepository.findById(eq(4L)))
                    .willReturn(Optional.of(deactivatedPrompt));

            // When & Then
            assertThatThrownBy(() -> promptService.deactivatePrompt(4L, mockUser))
                    .isInstanceOf(CustomException.class)
                    .satisfies(ex -> {
                        CustomException customEx = (CustomException) ex;
                        assertThat(customEx.getMessage()).isEqualTo(ErrorCode.PROMPT_ALREADY_DEACTIVATED.getMessage());
                        assertThat(customEx.getHttpStatus()).isEqualTo(ErrorCode.PROMPT_ALREADY_DEACTIVATED.getHttpStatus());
                    });

            verify(promptRepository, times(1)).findById(4L);
            verify(questionRepository, never()).findAllByPromptAndIsActiveTrue(any(Prompt.class));
        }

        @Test
        @DisplayName("프롬프트에 연결된 질문이 없는 경우에도 성공적으로 비활성화한다")
        void deactivatePrompt_NoQuestionsLinked_SuccessfullyDeactivatesPrompt() {
            // Given
            given(promptRepository.findById(eq(1L))).willReturn(Optional.of(mockPrompt1));
            given(questionRepository.findAllByPromptAndIsActiveTrue(mockPrompt1)).willReturn(Collections.emptyList());

            // When
            promptService.deactivatePrompt(1L, mockUser);

            // Then
            assertThat(mockPrompt1.isActive()).isFalse();
            verify(promptRepository, times(1)).findById(1L);
            verify(questionRepository, times(1)).findAllByPromptAndIsActiveTrue(mockPrompt1);
        }

    }

}