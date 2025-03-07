package com.interviewmate.be.auth.application;

import com.interviewmate.be.auth.domain.OAuth2UserInfo;
import com.interviewmate.be.auth.domain.OAuth2UserInfoFactory;
import com.interviewmate.be.auth.domain.OAuth2UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

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
     * @param oAuth2UserRequest OAuth2UserRequest 객체 (OAuth2 로그인 요청 정보 포함)
     * @return OAuth2User 객체 (Spring Security에서 관리하는 OAuth2 사용자 정보)
     * @throws OAuth2AuthenticationException 인증 실패 시 발생하는 예외
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        String provider = oAuth2UserRequest.getClientRegistration().getRegistrationId();

        log.info("CustomOAuth2UserService: provider={}", provider);

        // 제공자별 사용자 정보 객체 생성
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, oAuth2User.getAttributes());

        log.info("OAuth2 로그인: provider={}, userId={}, email={}", provider, userInfo.getId(), userInfo.getEmail());

        return new OAuth2UserPrincipal(provider, oAuth2User);
    }

}
