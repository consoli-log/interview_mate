package com.interviewmate.be.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * packageName    : com.interviewmate.be.common.config
 * fileName       : RedisConfig
 * author         : eumsoli
 * date           : 2025-03-20
 * description    : Redis 설정 클래스
 */
@Configuration
public class RedisConfig {

    private final String redisUrl;

    public RedisConfig(
            @Value("${spring.data.redis.url}") String redisUrl
    ) {
        this.redisUrl = redisUrl;
    }

    /**
     * methodName : redisConnectionFactory
     * description : Redis 연결 팩토리 설정 (URL 기반)
     *
     * @return RedisConnectionFactory Redis 연결 팩토리 객체
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(
                LettuceConnectionFactory.createRedisConfiguration(redisUrl));
    }

    /**
     * methodName : redisTemplate
     * description : RedisTemplate 설정 (Key-Value 저장소)
     *
     * @return RedisTemplate<String, String> 문자열 기반 Redis 템플릿 객체
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());   // key: 문자열
        redisTemplate.setValueSerializer(new StringRedisSerializer()); // value: 문자열

        return redisTemplate;
    }

}
