package com.interviewmate.be.common.config;

import com.interviewmate.be.auth.application.OAuth2UserService;
import com.interviewmate.be.auth.event.OAuth2FailureHandler;
import com.interviewmate.be.auth.event.OAuth2SuccessHandler;
import com.interviewmate.be.common.security.JwtAccessDeniedHandler;
import com.interviewmate.be.common.security.JwtAuthenticationEntryPoint;
import com.interviewmate.be.common.security.JwtTokenProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * packageName    : com.interviewmate.be.config
 * fileName       : TestSecurityConfig
 * author         : eumsoli
 * date           : 2025-03-21
 * description    : 테스트 환경에서 Spring Security 비활성화
 */
@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

}
