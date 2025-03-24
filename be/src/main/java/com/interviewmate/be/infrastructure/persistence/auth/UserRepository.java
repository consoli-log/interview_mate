package com.interviewmate.be.infrastructure.persistence.auth;

import com.interviewmate.be.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * packageName    : com.interviewmate.be.infrastructure.persistence.auth
 * fileName       : UserRepository
 * author         : eumsoli
 * date           : 2025-03-17
 * description    : 사용자 정보를 관리하는 JPA 리포지토리
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * methodName : findByEmail
     * description : 이메일을 기반으로 사용자 정보 조회
     *
     * @param email 사용자 이메일
     * @return Optional<User> 조회된 사용자 정보
     */
    Optional<User> findByEmail(String email);

    /**
     * methodName : findByProviderId
     * description : 소셜 로그인 제공자 ID를 기반으로 사용자 정보 조회
     *
     * @param providerId 소셜 로그인 제공자 ID
     * @return Optional<User> 조회된 사용자 정보
     */
    Optional<User> findByProviderId(String providerId);

}

