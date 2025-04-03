package com.interviewmate.be.infrastructure.persistence.question;

import com.interviewmate.be.prompt.domain.Prompt;
import com.interviewmate.be.question.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

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
     * @return Integer 가장 큰 질문 번호 (없으면 0)
     */
    @Query("SELECT COALESCE(MAX(q.number), 0) FROM Question q WHERE q.prompt = :prompt AND q.isActive = true")
    int findMaxNumberByPrompt(Prompt prompt);

    /**
     * methodName : existsByPromptAndIsActiveFalse
     * description : 해당 프롬프트에 비활성화된 질문이 존재하는지 여부 확인
     *
     * @param prompt 대상 프롬프트
     * @return boolean true일 경우 비활성 질문 존재
     */
    boolean existsByPromptAndIsActiveFalse(Prompt prompt);

    /**
     * methodName : findAllByPromptAndIsActiveTrue
     * description : 해당 프롬프트에 속한 활성 질문들을 조회
     *
     * @param prompt 프롬프트 엔티티
     * @return List<Question> 활성 질문 리스트
     */
    List<Question> findAllByPromptAndIsActiveTrue(Prompt prompt);

    /**
     * methodName : findAllByPromptAndIsActiveTrueOrderByNumber
     * description : 특정 프롬프트의 활성화된 질문들을 번호 순으로 조회
     *
     * @param prompt 대상 프롬프트
     * @return List<Question> 활성화된 질문 리스트
     */
    List<Question> findAllByPromptAndIsActiveTrueOrderByNumber(Prompt prompt);

    /**
     * methodName : deleteByIsActiveFalseAndUpdatedAtBefore
     * description : 비활성화 상태이고 지정된 시각보다 오래된 질문을 삭제
     *
     * @param updatedAt 삭제 기준 시간
     * @return int 삭제된 질문 수
     */
    @Modifying
    @Query("DELETE FROM Question q WHERE q.isActive = false AND q.updatedAt < :updatedAt")
    int deleteByIsActiveFalseAndUpdatedAtBefore(LocalDateTime updatedAt);
}
