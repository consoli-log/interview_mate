package com.interviewmate.be.question.application;

import com.interviewmate.be.infrastructure.persistence.question.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * packageName    : com.interviewmate.be.question.application
 * fileName       : QuestionScheduler
 * author         : eumsoli
 * date           : 2025-03-26
 * description    : 1개월 지난 비활성 질문 자동 삭제 스케줄러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class QuestionScheduler {

    private final QuestionRepository questionRepository;

    /**
     * methodName : deleteOldInactiveQuestions
     * description : 비활성화된 지 1개월이 지난 질문을 자동으로 삭제한다.
     */
    @Scheduled(cron = "0 0 3 * * *") // 매일 새벽 3시
    public void deleteOldInactiveQuestions() {
        LocalDateTime expired = LocalDateTime.now().minusMonths(1);
        int deleted = questionRepository.deleteByIsActiveFalseAndUpdatedAtBefore(expired);
        log.info("[질문 자동 삭제] 1개월 지난 비활성 질문 {}개 삭제 완료", deleted);
    }

}
