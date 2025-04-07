package com.interviewmate.be.common.security;

import com.interviewmate.be.auth.application.OAuth2UserService;
import com.interviewmate.be.auth.event.OAuth2FailureHandler;
import com.interviewmate.be.auth.event.OAuth2SuccessHandler;
import com.interviewmate.be.infrastructure.persistence.auth.UserRepository;
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
    private final OAuth2UserService oAuth2UserService;
    private final UserRepository userRepository;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    /**
     * methodName : securityFilterChain
     * description : Spring Security 필터 체인을 설정 (OAuth2 및 JWT 기반 보안)
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
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable())

                // 세션을 사용하지 않도록 설정 (Stateless)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 요청별 보안 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/api/auth/**").permitAll() // 인증 관련 엔드포인트는 모두 허용
                        .requestMatchers("/api/questions/generate").permitAll() // 질문 생성 엔드포인트는 모두 허용
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // swagger 관련 엔드포인트는 모두 허용
                        .anyRequest().authenticated() // 나머지는 인증 필요
                )

                // OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService)) // 커스텀 OAuth2 서비스 등록
                        .successHandler(oAuth2SuccessHandler) // OAuth2 로그인 성공 시 핸들러
                        .failureHandler(oAuth2FailureHandler) // OAuth2 로그인 실패 시 핸들러
                )

                // 인증 실패 처리
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )

                // JWT 필터 추가
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);  // JWT 필터 추가

        return http.build();
    }

    /**
     * methodName : jwtAuthenticationFilter
     * description : JWT 인증 필터 빈 등록
     *
     * @return JwtAuthenticationFilter JWT 인증 필터
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, userRepository);
    }

}
