package com.interviewmate.be.auth.application;

import com.interviewmate.be.auth.domain.OAuth2UserInfo;
import com.interviewmate.be.auth.domain.OAuth2UserInfoFactory;
import com.interviewmate.be.auth.domain.OAuth2UserPrincipal;
import com.interviewmate.be.auth.domain.User;
import com.interviewmate.be.infrastructure.persistence.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * packageName    : com.interviewmate.be.auth.application
 * fileName       : OAuth2UserService
 * author         : eumsoli
 * date           : 2025-03-07
 * description    : OAuth2 로그인 시 사용자 정보를 가져와 회원 가입 또는 로그인 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    /**
     * methodName : loadUser
     * description : OAuth2 로그인 시 사용자 정보를 로드하여 처리하는 메서드
     *
     * @param oAuth2UserRequest OAuth2UserRequest 객체 (OAuth2 로그인 요청 정보 포함)
     * @return OAuth2User 객체 (Spring Security에서 관리하는 OAuth2 사용자 정보)
     * @throws OAuth2AuthenticationException 인증 실패 시 발생하는 예외
     */
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        // OAuth2 제공자 ID 가져오기 (google, kakao 등)
        String provider = oAuth2UserRequest.getClientRegistration().getRegistrationId();

        log.info("OAuth2UserService: provider={}", provider);

        // OAuth2 제공자별로 사용자 정보 가져오기
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, oAuth2User.getAttributes());

        // 이메일 또는 providerId를 기준으로 기존 유저 검색
        Optional<User> userOptional = userRepository.findByEmail(userInfo.getEmail());
        User user;

        if (userOptional.isPresent()) {
            // 기존 회원이면 그대로 반환
            user = userOptional.get();
        } else {
            // 신규 회원 가입
            user = User.builder()
                    .email(userInfo.getEmail())
                    .name(userInfo.getName())
                    .providerId(userInfo.getId())
                    .provider(provider)
                    .build();
            userRepository.save(user);
        }

        log.info("OAuth2 로그인: provider={}, userId={}, email={}", provider, userInfo.getId(), userInfo.getEmail());

        return new OAuth2UserPrincipal(provider, oAuth2User);

    }

}
