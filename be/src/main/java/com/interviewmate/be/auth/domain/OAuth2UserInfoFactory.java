package com.interviewmate.be.auth.domain;

import java.util.Map;

/**
 * packageName    : com.interviewmate.be.auth.domain
 * fileName       : OAuth2UserInfoFactory
 * author         : eumsoli
 * date           : 2025-03-07
 * description    : OAuth2 제공자별 사용자 정보를 처리하는 팩토리 클래스
 */
public class OAuth2UserInfoFactory {

    /**
     * methodName : getOAuth2UserInfo
     * description : OAuth2 제공자별로 적절한 OAuth2UserInfo 객체를 반환하는 메서드
     *
     * @param provider OAuth2 제공자 (google, kakao 등)
     * @param attributes OAuth2 사용자 정보 Map
     * @return OAuth2UserInfo 제공자별 사용자 정보 객체
     */
    public static OAuth2UserInfo getOAuth2UserInfo(String provider, Map<String, Object> attributes) {
        if ("google".equals(provider)) {
            return new GoogleOAuth2UserInfo(attributes);
        } else if ("kakao".equals(provider)) {
            return new KakaoOAuth2UserInfo(attributes);
        }
        throw new IllegalArgumentException("지원하지 않는 OAuth2 제공자: " + provider);
    }
}
