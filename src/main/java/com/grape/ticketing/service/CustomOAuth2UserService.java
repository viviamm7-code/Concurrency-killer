package com.grape.ticketing.service;

import com.grape.ticketing.domain.member.AuthProvider;
import com.grape.ticketing.domain.member.Member;
import com.grape.ticketing.domain.member.SocialAccount;
import com.grape.ticketing.repository.MemberRepository;
import com.grape.ticketing.repository.SocialAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
// 네이버, 카카오 로그인 서비스
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        if ("naver".equals(registrationId)) {
            return processNaverUser(attributes);
        }

        if ("kakao".equals(registrationId)) {
            return processKakaoUser(attributes);
        }

        throw new OAuth2AuthenticationException("지원하지 않는 OAuth2 로그인입니다. registrationId=" + registrationId);
    }

    private OAuth2User processNaverUser(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        if (response == null) {
            throw new OAuth2AuthenticationException("네이버 사용자 정보(response)가 없습니다.");
        }

        String providerId = String.valueOf(response.get("id"));
        String email = (String) response.get("email");
        String name = (String) response.get("name");

        SocialAccount socialAccount = socialAccountRepository
                .findByProviderAndProviderId(AuthProvider.NAVER, providerId)
                .orElseGet(() -> createNaverAccount(providerId, email, name));

        Member member = socialAccount.getMember();

        Map<String, Object> customAttributes = new HashMap<>(attributes);
        customAttributes.put("memberId", member.getId());
        customAttributes.put("email", member.getEmail());

        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority(member.getRole())),
                customAttributes,
                "response"
        );
    }

    private OAuth2User processKakaoUser(Map<String, Object> attributes) {
        String providerId = String.valueOf(attributes.get("id"));

        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");

        if (providerId == null || "null".equals(providerId)) {
            throw new OAuth2AuthenticationException("카카오 사용자 정보(id)가 없습니다.");
        }

        String extractedEmail = null;
        String extractedName = null;

        if (kakaoAccount != null) {
            extractedEmail = (String) kakaoAccount.get("email");
        }

        if (properties != null) {
            extractedName = (String) properties.get("nickname");
        }

        final String email = extractedEmail;
        final String name = extractedName;

        SocialAccount socialAccount = socialAccountRepository
                .findByProviderAndProviderId(AuthProvider.KAKAO, providerId)
                .orElseGet(() -> createKakaoAccount(providerId, email, name));

        Member member = socialAccount.getMember();

        Map<String, Object> customAttributes = new HashMap<>(attributes);
        customAttributes.put("memberId", member.getId());
        customAttributes.put("email", member.getEmail());

        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority(member.getRole())),
                customAttributes,
                "id"
        );
    }

    private SocialAccount createNaverAccount(String providerId, String email, String name) {
        String safeEmail = (email != null && !email.isBlank())
                ? email
                : "naver_" + providerId + "@social.local";

        String username = "naver_" + providerId;
        String encodedPassword = passwordEncoder.encode(UUID.randomUUID().toString());

        Member member = new Member(
                name != null ? name : "naver_user",
                safeEmail,
                username,
                encodedPassword,
                "ROLE_USER"
        );

        Member savedMember = memberRepository.save(member);

        SocialAccount socialAccount = SocialAccount.builder()
                .provider(AuthProvider.NAVER)
                .providerId(providerId)
                .email(safeEmail)
                .member(savedMember)
                .build();

        return socialAccountRepository.save(socialAccount);
    }

    private SocialAccount createKakaoAccount(String providerId, String email, String name) {
        String safeEmail = (email != null && !email.isBlank())
                ? email
                : "kakao_" + providerId + "@social.local";

        String username = "kakao_" + providerId;
        String encodedPassword = passwordEncoder.encode(UUID.randomUUID().toString());

        Member member = new Member(
                name != null ? name : "kakao_user",
                safeEmail,
                username,
                encodedPassword,
                "ROLE_USER"
        );

        Member savedMember = memberRepository.save(member);

        SocialAccount socialAccount = SocialAccount.builder()
                .provider(AuthProvider.KAKAO)
                .providerId(providerId)
                .email(safeEmail)
                .member(savedMember)
                .build();

        return socialAccountRepository.save(socialAccount);
    }
}