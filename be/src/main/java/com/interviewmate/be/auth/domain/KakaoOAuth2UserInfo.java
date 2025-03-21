package com.interviewmate.be.auth.domain;

import java.util.Map;

/**
 * packageName    : com.interviewmate.be.auth.domain
 * fileName       : KakaoOAuth2UserInfo
 * author         : eumsoli
 * date           : 2025-03-07
 * description    : 카카오 OAuth2 사용자 정보를 처리하는 클래스
 */
public class KakaoOAuth2UserInfo extends OAuth2UserInfo {

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    /**
     * methodName : getId
     * description : 카카오 OAuth2 사용자 고유 ID 반환 (id)
     *
     * @return 사용자 ID
     */
    @Override
    public String getId() {
        return String.valueOf(attributes.get("id"));
    }

    /**
     * methodName : getEmail
     * description : 카카오 OAuth2 사용자 이메일 반환
     *
     * @return 사용자 이메일
     */
    @Override
    public String getEmail() {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        return (String) account.get("email");
    }

    /**
     * methodName : getName
     * description : 카카오 OAuth2 사용자 이름 반환
     *
     * @return 사용자 이름
     */
    @Override
    public String getName() {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        return (String) properties.get("nickname");
    }

}