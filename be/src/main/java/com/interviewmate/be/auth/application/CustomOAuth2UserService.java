package com.interviewmate.be.auth.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * packageName    : com.interviewmate.be.auth.application
 * fileName       : CustomOAuth2UserService
 * author         : eumsoli
 * date           : 2025-03-07
 * description    : OAuth2 로그인 시 사용자 정보를 가져와 회원 가입 또는 로그인 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    /**
     * methodName : loadUser
     * description : OAuth2 로그인 시 사용자 정보를 로드하여 처리하는 메서드
     *
     * @param userRequest OAuth2UserRequest 객체 (OAuth2 로그인 요청 정보 포함)
     * @return OAuth2User 객체 (Spring Security에서 관리하는 OAuth2 사용자 정보)
     * @throws OAuth2AuthenticationException 인증 실패 시 발생하는 예외
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();

        // OAuth2 제공자별로 사용자 정보 매핑 (구글, 카카오 등)
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String userId = extractUserId(provider, attributes);

        log.info("OAuth2 로그인: provider={}, userId={}", provider, userId);

        // 사용자 ID 키를 동적으로 설정 (구글: "sub", 카카오: "id")
        String userIdKey = "google".equals(provider) ? "sub" : "id";

        return new DefaultOAuth2User(oAuth2User.getAuthorities(), attributes, userIdKey);
    }

    /**
     * methodName : extractUserId
     * description : OAuth2 제공자별로 사용자 ID를 추출하는 메서드
     *
     * @param provider OAuth2 제공자 (google, kakao 등)
     * @param attributes 사용자 정보 Map
     * @return 사용자 ID (구글: sub, 카카오: id)
     */
    private String extractUserId(String provider, Map<String, Object> attributes) {
        if ("google".equals(provider)) {
            return (String) attributes.get("sub");
        } else if ("kakao".equals(provider)) {
            return String.valueOf(attributes.get("id"));
        }
        throw new OAuth2AuthenticationException("지원하지 않는 OAuth2 제공자: " + provider);
    }

}
