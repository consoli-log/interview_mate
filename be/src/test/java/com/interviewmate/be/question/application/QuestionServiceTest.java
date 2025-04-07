package com.interviewmate.be.question.application;

import com.interviewmate.be.auth.domain.User;
import com.interviewmate.be.common.exception.CustomException;
import com.interviewmate.be.common.exception.ErrorCode;
import com.interviewmate.be.infrastructure.openai.GeminiClient;
import com.interviewmate.be.infrastructure.persistence.prompt.PromptRepository;
import com.interviewmate.be.infrastructure.persistence.question.QuestionRepository;
import com.interviewmate.be.prompt.application.PromptService;
import com.interviewmate.be.prompt.domain.Prompt;
import com.interviewmate.be.question.domain.Question;
import com.interviewmate.be.question.dto.QuestionGenerateRequest;
import com.interviewmate.be.question.dto.QuestionListResponse;
import com.interviewmate.be.question.dto.QuestionRegenerateRequest;
import com.interviewmate.be.question.dto.QuestionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * packageName    : com.interviewmate.be.question.application
 * fileName       : QuestionServiceTest
 * author         : eumsoli
 * date           : 2025-03-30
 * description    : QuestionService의 비즈니스 로직을 테스트하는 클래스
 */
@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock
    private GeminiClient geminiClient;

    @Mock
    private PromptService promptService;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private PromptRepository promptRepository;

    @InjectMocks
    private QuestionService questionService;

    private User mockUser;
    private Prompt mockPrompt;
    private Question mockQuestion;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .email("soli@test.com")
                .name("테스트 사용자")
                .provider("google")
                .providerId("1234567890")
                .build();
        ReflectionTestUtils.setField(mockUser, "id", 1L);

        mockPrompt = Prompt.builder()
                .user(mockUser)
                .title("테스트 프롬프트 제목")
                .prompt("테스트 프롬프트 내용")
                .build();
        ReflectionTestUtils.setField(mockPrompt, "id", 1L);
        ReflectionTestUtils.setField(mockPrompt, "isActive", true);

        mockQuestion = Question.builder()
                .prompt(mockPrompt)
                .question("질문 1")
                .number(1)
                .build();
        ReflectionTestUtils.setField(mockQuestion, "id", 100L);
        ReflectionTestUtils.setField(mockQuestion, "isActive", true);
    }

    @Test
    @DisplayName("비회원이 질문 생성 요청 시 저장 없이 결과만 응답한다")
    void generateQuestions_GuestUser_ReturnsQuestionsWithoutSaving() {
        // Given
        List<String> generated = List.of("질문 1", "질문 2", "질문 3");
        given(geminiClient.generateQuestions("프롬프트")).willReturn(generated);

        QuestionGenerateRequest request = new QuestionGenerateRequest("프롬프트");

        // When
        List<QuestionResponse> result = questionService.generateQuestions(request, null);

        // Then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).question()).isEqualTo("질문 1");
        verify(questionRepository, never()).save(any());
    }

    @Test
    @DisplayName("회원이 질문 생성 요청 시 프롬프트와 질문을 저장하고 응답한다")
    void generateQuestions_AuthenticatedUser_SavesPromptAndQuestions() {
        // Given
        List<String> generated = List.of("질문 A", "질문 B");
        given(geminiClient.generateQuestions(any())).willReturn(generated);
        given(promptService.savePrompt(eq(mockUser), any())).willReturn(mockPrompt);
        given(questionRepository.findMaxNumberByPrompt(mockPrompt)).willReturn(0);
        given(questionRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

        QuestionGenerateRequest request = new QuestionGenerateRequest("프롬프트");

        // When
        List<QuestionResponse> result = questionService.generateQuestions(request, mockUser);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(1).number()).isEqualTo(2);
        assertThat(result.get(1).question()).isEqualTo("질문 B");
        verify(questionRepository, times(2)).save(any());
    }

    @Test
    @DisplayName("질문 삭제 요청 시 권한 확인 후 비활성화한다")
    void deactivateQuestion_ValidUser_DeletesSuccessfully() {
        // Given
        given(questionRepository.findById(100L)).willReturn(Optional.of(mockQuestion));

        // When
        questionService.deactivateQuestion(100L, mockUser);

        // Then
        assertThat(mockQuestion.isActive()).isFalse();
    }

    @Test
    @DisplayName("다른 사용자가 질문 삭제 시 예외가 발생한다")
    void deactivateQuestion_AnotherUser_ThrowsAccessDenied() {
        // Given
        User another = User.builder().email("xx@test.com").provider("kakao").providerId("222").build();
        ReflectionTestUtils.setField(another, "id", 99L);

        given(questionRepository.findById(100L)).willReturn(Optional.of(mockQuestion));

        // When & Then
        assertThatThrownBy(() -> questionService.deactivateQuestion(100L, another))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.QUESTION_ACCESS_DENIED.getMessage());
    }

    @Test
    @DisplayName("비활성화된 질문을 다시 비활성화하려 하면 예외가 발생한다")
    void deactivateQuestion_AlreadyDeactivated_ThrowsException() {
        // Given
        ReflectionTestUtils.setField(mockQuestion, "isActive", false);
        given(questionRepository.findById(100L)).willReturn(Optional.of(mockQuestion));

        // When & Then
        assertThatThrownBy(() -> questionService.deactivateQuestion(100L, mockUser))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.QUESTION_ALREADY_DEACTIVATED.getMessage());
    }

    @Test
    @DisplayName("질문 재생성 시 새로운 질문을 하나 저장 후 반환한다")
    void regenerateQuestion_WithInactiveExists_SuccessfullySavesNew() {
        // Given
        QuestionRegenerateRequest request = new QuestionRegenerateRequest(1L);
        given(promptRepository.findById(1L)).willReturn(Optional.of(mockPrompt));
        given(questionRepository.existsByPromptAndIsActiveFalse(mockPrompt)).willReturn(true);
        given(geminiClient.generateSingleQuestion(any())).willReturn("새로운 질문");
        given(questionRepository.findMaxNumberByPrompt(mockPrompt)).willReturn(3);
        given(questionRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

        // When
        QuestionResponse result = questionService.regenerateQuestion(request, mockUser);

        // Then
        assertThat(result.number()).isEqualTo(4);
        assertThat(result.question()).isEqualTo("새로운 질문");
    }

    @Test
    @DisplayName("질문 재생성 시 비활성화 질문이 없으면 예외가 발생한다")
    void regenerateQuestion_NoInactive_ThrowsException() {
        // Given
        QuestionRegenerateRequest request = new QuestionRegenerateRequest(1L);
        given(promptRepository.findById(1L)).willReturn(Optional.of(mockPrompt));
        given(questionRepository.existsByPromptAndIsActiveFalse(mockPrompt)).willReturn(false);

        // When & Then
        assertThatThrownBy(() -> questionService.regenerateQuestion(request, mockUser))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.QUESTION_REGENERATION_NOT_ALLOWED.getMessage());
    }

    @Test
    @DisplayName("활성 질문 조회 시 정상적으로 리스트를 반환한다")
    void getActiveQuestions_ReturnsQuestionList() {
        // Given
        List<Question> questionList = List.of(
                new Question(mockPrompt, 1, "질문1"),
                new Question(mockPrompt, 2, "질문2")
        );
        given(promptRepository.findById(1L)).willReturn(Optional.of(mockPrompt));
        given(questionRepository.findAllByPromptAndIsActiveTrueOrderByNumber(mockPrompt)).willReturn(questionList);

        // When
        QuestionListResponse result = questionService.getActiveQuestions(1L, mockUser);

        // Then
        assertThat(result.questions()).hasSize(2);
        assertThat(result.questions().get(0).number()).isEqualTo(1);
        assertThat(result.questions().get(1).question()).isEqualTo("질문2");
    }

}