package com.interviewmate.be.auth.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * packageName    : com.interviewmate.be.auth.domain
 * fileName       : User
 * author         : eumsoli
 * date           : 2025-03-17
 * description    : 사용자 엔티티 클래스
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_seq")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(name = "provider_id",nullable = false)
    private String providerId;

    @Column(nullable = false)
    private String provider;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 등록일

    /**
     * methodName : User
     * description : User 생성자 (Builder 패턴 적용)
     *
     * @param email    사용자 이메일
     * @param name     사용자 이름
     * @param providerId 소셜 로그인 제공자 ID
     * @param provider 소셜 로그인 제공자
     */
    @Builder
    public User(String email, String name, String providerId, String provider) {
        this.email = email;
        this.name = name;
        this.providerId  = providerId ;
        this.provider = provider;
    }

}
