package com.interviewmate.be.auth.domain;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

/**
 * packageName    : com.interviewmate.be.auth.domain
 * fileName       : OAuth2UserPrincipal
 * author         : eumsoli
 * date           : 2025-03-07
 * description    : OAuth2 로그인 사용자 정보를 담는 Principal 객체
 */
@Getter
public class OAuth2UserPrincipal implements OAuth2User {

    private final String provider;
    private final OAuth2User oAuth2User;

    public OAuth2UserPrincipal(String provider, OAuth2User oAuth2User) {
        this.provider = provider;
        this.oAuth2User = oAuth2User;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oAuth2User.getAuthorities();
    }

    @Override
    public String getName() {
        return oAuth2User.getName();
    }

}


