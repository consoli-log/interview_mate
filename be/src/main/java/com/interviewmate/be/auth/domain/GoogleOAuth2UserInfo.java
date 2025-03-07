package com.interviewmate.be.auth.domain;

import java.util.Map;

/**
 * packageName    : com.interviewmate.be.auth.domain
 * fileName       : GoogleOAuth2UserInfo
 * author         : eumsoli
 * date           : 2025-03-07
 * description    : 구글 OAuth2 사용자 정보를 처리하는 클래스
 */
public class GoogleOAuth2UserInfo extends OAuth2UserInfo {

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    /**
     * methodName : getId
     * description : 구글 OAuth2 사용자 고유 ID 반환 (sub)
     *
     * @return 사용자 ID
     */
    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }

    /**
     * methodName : getEmail
     * description : 구글 OAuth2 사용자 이메일 반환
     *
     * @return 사용자 이메일
     */
    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    /**
     * methodName : getName
     * description : 구글 OAuth2 사용자 이름 반환
     *
     * @return 사용자 이름
     */
    @Override
    public String getName() {
        return (String) attributes.get("name");
    }
}
