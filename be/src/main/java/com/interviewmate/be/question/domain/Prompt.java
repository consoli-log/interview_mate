package com.interviewmate.be.question.domain;

import com.interviewmate.be.auth.domain.User;
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
    @Column(name = "prompt_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_seq")
    private User user; // 사용자와의 연관 관계

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String prompt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 작성일

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 수정일

    /**
     * methodName : Prompt
     * description : Prompt 생성자 (Builder 패턴 적용)
     *
     * @param user    연관된 사용자
     * @param title   요약 제목
     * @param prompt  프롬프트 내용
     */
    @Builder
    public Prompt(User user, String title, String prompt) {
        this.user = user;
        this.title = title;
        this.prompt = prompt;
    }

}
