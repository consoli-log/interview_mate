package com.interviewmate.be.question.domain;

import com.interviewmate.be.prompt.domain.Prompt;
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
 * fileName       : Question
 * author         : eumsoli
 * date           : 2025-03-25
 * description    : 사용자가 입력한 프롬프트에 대한 질문 엔티티 클래스
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prompt_seq", nullable = false)
    private Prompt prompt; // 프롬프트와의 연관 관계

    @Column(nullable = false)
    private int number; // 프롬프트 내 질문 번호 (1~5, 혹은 6...)

    @Lob
    @Column(nullable = false)
    private String question; // 질문

    @Column(name = "is_active")
    private boolean isActive; // 비활성화 여부

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 생성일

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 수정일


    /**
     * constructor : Question 생성자
     * description : Question 객체 생성
     *
     * @param prompt  연관된 프롬프트
     * @param number  질문 번호
     * @param question 질문 내용
     */
    @Builder
    public Question(Prompt prompt, int number, String question) {
        this.prompt = prompt;
        this.number = number;
        this.question = question;
        this.isActive = true;
    }

    /**
     * methodName : deactivate
     * description : 질문을 비활성화 상태로 변경
     */
    public void deactivate() {
        this.isActive = false;
    }

}
