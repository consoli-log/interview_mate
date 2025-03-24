package com.interviewmate.be.auth.application;

import com.interviewmate.be.auth.domain.OAuth2UserInfo;
import com.interviewmate.be.auth.domain.OAuth2UserInfoFactory;
import com.interviewmate.be.auth.domain.OAuth2UserPrincipal;
import com.interviewmate.be.auth.domain.User;
import com.interviewmate.be.common.exception.CustomException;
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

import static org.springframework.http.HttpStatus.CONFLICT;

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
        String provider = oAuth2UserRequest.getClientRegistration().getRegistrationId().toLowerCase();

        log.info("OAuth2UserService: provider={}", provider);

        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, oAuth2User.getAttributes());

        String providerId = userInfo.getId();
        String email = userInfo.getEmail();
        String name = userInfo.getName();

        // 1️⃣ 이메일로 사용자 존재 여부 확인
        Optional<User> sameEmailUser = userRepository.findByEmail(email);

        if (sameEmailUser.isPresent()) {
            User existingUser = sameEmailUser.get();

            // 2️⃣ 이메일은 같지만 소셜 제공자가 다른 경우 예외 처리
            if (!existingUser.getProvider().equalsIgnoreCase(provider)) {
                log.warn("소셜 제공자 불일치: 기존={}, 요청={}", existingUser.getProvider(), provider);

                throw new CustomException(
                        CONFLICT,
                        String.format("해당 이메일은 '%s' 계정으로 이미 가입되어 있습니다. 기존 계정으로 로그인해 주세요.", existingUser.getProvider())
                );
            }

            return new OAuth2UserPrincipal(provider, oAuth2User); // 기존 사용자
        }

        // 3️⃣ 신규 사용자 가입
        User newUser = User.builder()
                .email(email)
                .name(name)
                .providerId(providerId)
                .provider(provider)
                .build();

        userRepository.save(newUser);

        log.info("신규 회원 가입: provider={}, providerId={}, email={}", provider, providerId, email);

        return new OAuth2UserPrincipal(provider, oAuth2User);
    }

}
