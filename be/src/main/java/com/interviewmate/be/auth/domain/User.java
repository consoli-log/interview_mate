package com.interviewmate.be.auth.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * packageName    : com.interviewmate.be.auth.domain
 * fileName       : User
 * author         : eumsoli
 * date           : 2025-03-17
 * description    : 사용자 엔티티 클래스
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String provider;
}
