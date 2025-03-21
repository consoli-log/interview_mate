package com.interviewmate.be.auth.domain;

import java.util.Map;

/**
 * packageName    : com.interviewmate.be.auth.domain
 * fileName       : OAuth2UserInfo
 * author         : eumsoli
 * date           : 2025-03-07
 * description    : OAuth2 제공자별 사용자 정보를 추상화한 클래스
 */
public abstract class OAuth2UserInfo {

    protected Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public abstract String getId(); // 사용자 ID (providerId)

    public abstract String getEmail(); // 사용자 이메일

    public abstract String getName(); // 사용자 이름

}
