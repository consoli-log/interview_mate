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

    /**
     * methodName : getId
     * description : OAuth2 제공자별 사용자 고유 ID 반환
     *
     * @return 사용자 ID
     */
    public abstract String getId();

    /**
     * methodName : getEmail
     * description : OAuth2 제공자별 사용자 이메일 반환
     *
     * @return 사용자 이메일
     */
    public abstract String getEmail();

    /**
     * methodName : getName
     * description : OAuth2 제공자별 사용자 이름 반환
     *
     * @return 사용자 이름
     */
    public abstract String getName();

}
