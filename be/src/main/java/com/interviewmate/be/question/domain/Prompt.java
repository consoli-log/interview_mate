package com.interviewmate.be.question.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * packageName    : com.interviewmate.be.question.domain
 * fileName       : Prompt
 * author         : eumsoli
 * date           : 2025-03-24
 * description    : 사용자가 입력한 프롬프트 엔티티 클래스
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Prompt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String prompt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 등록일

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 수정일

    /**
     * methodName : Prompt
     * description : Prompt 생성자 (Builder 패턴 적용)
     *
     * @param title   요약 제목
     * @param prompt  프롬프트 내용
     */
    @Builder
    public Prompt(String title, String prompt) {
        this.title = title;
        this.prompt = prompt;
    }

}
