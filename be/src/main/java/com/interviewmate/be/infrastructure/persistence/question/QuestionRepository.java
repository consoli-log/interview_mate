package com.interviewmate.be.infrastructure.persistence.question;

import com.interviewmate.be.question.domain.Prompt;
import com.interviewmate.be.question.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * packageName    : com.interviewmate.be.infrastructure.persistence.question
 * fileName       : QuestionRepository
 * author         : eumsoli
 * date           : 2025-03-25
 * description    : 프롬프트에 대한 질문 정보를 관리하는 JPA 리포지토리
 */
public interface QuestionRepository extends JpaRepository<Question, Long> {

    /**
     * methodName : findMaxNumberByPrompt
     * description : 특정 프롬프트에 대한 질문 중 가장 큰 번호 조회 (isActive = true만)
     *
     * @param prompt 대상 프롬프트
     * @return Integer 가장 큰 질문 번호 (없으면 null)
     */
    @Query("SELECT COALESCE(MAX(q.number), 0) FROM Question q WHERE q.prompt = :prompt AND q.isActive = true")
    Integer findMaxNumberByPrompt(Prompt prompt);


}
