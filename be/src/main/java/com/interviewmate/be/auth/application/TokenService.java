package com.interviewmate.be.auth.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * packageName    : com.interviewmate.be.auth.application
 * fileName       : TokenService
 * author         : eumsoli
 * date           : 2025-03-20
 * description    : Redis를 통한 Refresh Token 관련 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String REFRESH_TOKEN_PREFIX = "refresh:";
    private static final long REFRESH_TOKEN_EXPIRE_TIME_MS = 14 * 24 * 60 * 60 * 1000L;  // 14일 (밀리초)

    /**
     * methodName : saveRefreshToken
     * description : Redis에 Refresh Token 저장
     *
     * @param providerId  사용자 ID
     * @param refreshToken  발급된 Refresh Token
     */
    public void saveRefreshToken(String providerId, String refreshToken) {
        redisTemplate.opsForValue().set(REFRESH_TOKEN_PREFIX + providerId, refreshToken, REFRESH_TOKEN_EXPIRE_TIME_MS, TimeUnit.MILLISECONDS);
        log.info("Refresh Token 저장 완료: providerId={}, 만료시간={}", providerId, REFRESH_TOKEN_EXPIRE_TIME_MS);
        log.info("Redis 저장: refresh:{} = {}", providerId, refreshToken);
    }

    /**
     * methodName : getRefreshToken
     * description : Redis에서 Refresh Token 가져오기
     *
     * @param providerId 사용자 ID
     * @return String Refresh Token
     */
    public String getRefreshToken(String providerId ) {
        String token = redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + providerId);
        log.info("Redis 조회: refresh:{} = {}", providerId, token);
        return token;
    }

    /**
     * methodName : deleteRefreshToken
     * description : Redis에서 Refresh Token 삭제 (로그아웃 시)
     *
     * @param providerId  사용자 ID
     */
    public void deleteRefreshToken(String providerId ) {
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + providerId );
        log.info("Refresh Token 삭제 완료: userId={}", providerId );
    }

}
