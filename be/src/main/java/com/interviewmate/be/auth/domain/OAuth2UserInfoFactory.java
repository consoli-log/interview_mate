package com.interviewmate.be.auth.domain;

import com.interviewmate.be.common.exception.CustomException;
import com.interviewmate.be.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * packageName    : com.interviewmate.be.auth.domain
 * fileName       : OAuth2UserInfoFactory
 * author         : eumsoli
 * date           : 2025-03-07
 * description    : OAuth2 제공자별 사용자 정보를 처리하는 팩토리 클래스
 */
@Slf4j
public class OAuth2UserInfoFactory {

    /**
     * methodName : getOAuth2UserInfo
     * description : OAuth2 제공자별로 적절한 OAuth2UserInfo 객체를 반환하는 메서드
     *
     * @param provider   OAuth2 제공자 (google, kakao 등)
     * @param attributes OAuth2 사용자 정보 Map
     * @return OAuth2UserInfo 제공자별 사용자 정보 객체
     * @throws CustomException 지원하지 않는 제공자인 경우
     */
    public static OAuth2UserInfo getOAuth2UserInfo(String provider, Map<String, Object> attributes) {
        String lowerProvider = provider.toLowerCase();
        log.info("OAuth2UserInfoFactory: provider={}, attributes={}", lowerProvider, attributes);

        return switch (lowerProvider) {
            case "google" -> new GoogleOAuth2UserInfo(attributes);
            case "kakao" -> new KakaoOAuth2UserInfo(attributes);
            default -> throw new CustomException(ErrorCode.OAUTH2_CLIENT_ERROR);
        };
    }

}
