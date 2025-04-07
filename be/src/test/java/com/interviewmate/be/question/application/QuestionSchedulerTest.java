package com.interviewmate.be.question.application;

import com.interviewmate.be.infrastructure.persistence.question.QuestionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.*;

/**
 * packageName    : com.interviewmate.be.question.application
 * fileName       : QuestionSchedulerTest
 * author         : eumsoli
 * date           : 2025-03-30
 * description    : QuestionScheduler의 스케줄링 로직을 테스트하는 클래스
 */
@ExtendWith(MockitoExtension.class)
class QuestionSchedulerTest {

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QuestionScheduler questionScheduler;

    @Test
    @DisplayName("1개월 지난 비활성 질문을 자동 삭제한다")
    void deleteOldInactiveQuestions_DeletesSuccessfully() {
        // Given
        given(questionRepository.deleteByIsActiveFalseAndUpdatedAtBefore(any())).willReturn(3);

        // When
        questionScheduler.deleteOldInactiveQuestions();

        // Then
        verify(questionRepository, times(1)).deleteByIsActiveFalseAndUpdatedAtBefore(any());
    }

}