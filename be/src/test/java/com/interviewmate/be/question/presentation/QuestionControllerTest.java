package com.interviewmate.be.question.presentation;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

/**
 * packageName    : com.interviewmate.be.question.presentation
 * fileName       : QuestionControllerTest
 * author         : eumsoli
 * date           : 2025-04-01
 * description    : QuestionController의 API 동작을 단위 테스트하는 클래스
 */
@ExtendWith(MockitoExtension.class)
class QuestionControllerTest {

    @Mock
    private QuestionService questionService;

    @InjectMocks
    private QuestionController questionController;

    private User mockUser;
    private List<QuestionResponse> mockQuestionList;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .email("soli@test.com")
                .name("테스트 사용자")
                .provider("google")
                .providerId("1234567890")
                .build();

        ReflectionTestUtils.setField(mockUser, "id", 1L);

        // Mock 프롬프트 응답 생성
        mockQuestionList = Arrays.asList(
                new QuestionResponse(1, "질문 1"),
                new QuestionResponse(2, "질문 2")
        );
    }

    @Test
    @DisplayName("성공적으로 질문을 생성한다.")
    void generateQuestions_CreatesQuestionsSuccessfully() {
        // Given
        QuestionGenerateRequest request = new QuestionGenerateRequest("프롬프트");
        given(questionService.generateQuestions(request, mockUser)).willReturn(mockQuestionList);

        // When
        ResponseEntity<List<QuestionResponse>> result = questionController.generateQuestions(request, mockUser);

        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).hasSize(2);
    }

    @Test
    @DisplayName("회원은 성공적으로 질문을 비활성화 한다.")
    void deactivateQuestion_DeletesSuccessfully() {
        // Given
        Long questionId = 1L;
        willDoNothing().given(questionService).deactivateQuestion(questionId, mockUser);

        // When
        ResponseEntity<Void> result = questionController.deactivateQuestion(questionId, mockUser);

        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("비회원이 질문 비활성화 요청 시 예외가 발생한다")
    void deactivateQuestion_Unauthenticated_ThrowsException() {
        // When & Then
        assertThatThrownBy(() -> questionController.deactivateQuestion(1L, null))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.LOGIN_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("질문 재생성 시 서비스 호출 후 생성된 질문을 반환한다")
    void regenerateQuestion_ReturnsNewQuestion() {
        // Given
        QuestionRegenerateRequest request = new QuestionRegenerateRequest(1L);
        QuestionResponse mockResponse = new QuestionResponse(3, "새로운 질문");
        given(questionService.regenerateQuestion(request, mockUser)).willReturn(mockResponse);

        // When
        ResponseEntity<QuestionResponse> result = questionController.regenerateQuestion(request, mockUser);

        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody().question()).isEqualTo("새로운 질문");
    }

    @Test
    @DisplayName("비회원이 질문 재생성 요청 시 예외가 발생한다")
    void regenerateQuestion_Unauthenticated_ThrowsException() {
        // When & Then
        assertThatThrownBy(() -> questionController.regenerateQuestion(new QuestionRegenerateRequest(1L), null))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.LOGIN_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("프롬프트에 속한 질문 목록을 정상적으로 조회한다")
    void getQuestions_ReturnsQuestionList() {
        // Given
        QuestionListResponse mockResponse = new QuestionListResponse(List.of(
                new QuestionResponse(1, "질문1"),
                new QuestionResponse(2, "질문2")
        ));
        given(questionService.getActiveQuestions(1L, mockUser)).willReturn(mockResponse);

        // When
        ResponseEntity<QuestionListResponse> result = questionController.getQuestions(1L, mockUser);

        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().questions()).hasSize(2);
    }

    @Test
    @DisplayName("비회원이 질문 목록 요청 시 예외가 발생한다")
    void getQuestions_Unauthenticated_ThrowsException() {
        // When & Then
        assertThatThrownBy(() -> questionController.getQuestions(1L, null))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.LOGIN_REQUIRED.getMessage());
    }
}