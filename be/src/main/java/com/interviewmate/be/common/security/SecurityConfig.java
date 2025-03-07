package com.interviewmate.be.common.security;

import com.interviewmate.be.auth.application.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * packageName    : com.interviewmate.be.common.security
 * fileName       : SecurityConfig
 * author         : eumsoli
 * date           : 2025-03-07
 * description    : Spring Security 설정 클래스. OAuth2 기반 인증 및 JWT 설정을 포함.
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomOAuth2UserService customOAuth2UserService;

    /**
     * methodName : securityFilterChain
     * description : Spring Security 필터 체인을 설정. OAuth2 및 JWT 기반 보안 적용.
     *
     * @param http  HttpSecurity 객체로 보안 설정 적용
     * @return SecurityFilterChain 보안 필터 체인
     * @throws Exception 설정 중 발생할 수 있는 예외
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // CSRF 비활성화 (JWT 기반이므로 필요 없음)
                .csrf(csrf -> csrf.disable())

                // 세션을 사용하지 않도록 설정 (Stateless)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 요청별 보안 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll() // 인증 관련 엔드포인트는 모두 허용
                        .anyRequest().authenticated() // 나머지는 인증 필요
                )

                // OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService)) // 커스텀 OAuth2 서비스 등록
                        .defaultSuccessUrl("/api/auth/success") // 로그인 성공 시 이동할 URL
                        .failureUrl("/api/auth/failure") // 로그인 실패 시 이동할 URL
                )

                // 로그아웃 설정 - Stateless 방식이므로 서버에서는 별도 작업 없이 200 응답만 반환
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(200);
                        })
                )

                // JWT 필터 추가
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);  // JWT 필터 추가

        return http.build();
    }
}
