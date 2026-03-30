package com.grape.ticketing.service;

import com.grape.ticketing.domain.member.Member;
import com.grape.ticketing.domain.member.SocialAccount;
import com.grape.ticketing.domain.member.AuthProvider;
import com.grape.ticketing.repository.MemberRepository;
import com.grape.ticketing.repository.SocialAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;
    private final SocialAccountRepository socialAccountRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String providerId = (String) attributes.get("sub");
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        SocialAccount socialAccount = socialAccountRepository
                .findByProviderAndProviderId(AuthProvider.GOOGLE, providerId)
                .orElseGet(() -> createGoogleAccount(providerId, email, name));

        Member member = socialAccount.getMember();

        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority(member.getRole())),
                attributes,
                "sub"
        );
    }

    private SocialAccount createGoogleAccount(String providerId, String email, String name) {
        Member member = new Member(name, email, null, null, "ROLE");

        memberRepository.save(member);

        SocialAccount socialAccount = SocialAccount.builder()
                .provider(AuthProvider.GOOGLE)
                .providerId(providerId)
                .email(email)
                .member(member)
                .build();

        return socialAccountRepository.save(socialAccount);
    }
}