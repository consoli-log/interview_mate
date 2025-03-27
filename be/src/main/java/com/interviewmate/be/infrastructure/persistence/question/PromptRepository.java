package com.interviewmate.be.infrastructure.persistence.question;

import com.interviewmate.be.auth.domain.User;
import com.interviewmate.be.question.domain.Prompt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * packageName    : com.interviewmate.be.infrastructure.persistence.question
 * fileName       : PromptRepository
 * author         : eumsoli
 * date           : 2025-03-24
 * description    : 사용자가 입력한 프롬프트 정보를 관리하는 JPA 리포지토리
 */
public interface PromptRepository extends JpaRepository<Prompt, Long> {

    /**
     * methodName : findAllByUserAndIsActiveTrueOrderByCreatedAtDesc
     * description : 로그인 사용자의 활성 프롬프트들을 생성일 기준 최신순으로 조회
     *
     * @param user 사용자 엔티티
     * @return List<Prompt> 프롬프트 리스트
     */
    List<Prompt> findAllByUserAndIsActiveTrueOrderByCreatedAtDesc(User user);

}
