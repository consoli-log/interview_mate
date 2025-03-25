package com.interviewmate.be.infrastructure.persistence.question;

import com.interviewmate.be.question.domain.Prompt;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * packageName    : com.interviewmate.be.infrastructure.persistence.question
 * fileName       : PromptRepository
 * author         : eumsoli
 * date           : 2025-03-24
 * description    : 사용자가 입력한 프롬프트 정보를 관리하는 JPA 리포지토리
 */
public interface PromptRepository extends JpaRepository<Prompt, Long> {
}
