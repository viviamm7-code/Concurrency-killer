package com.grape.ticketing.service;

import com.grape.ticketing.domain.member.AuthProvider;
import com.grape.ticketing.domain.member.Member;
import com.grape.ticketing.domain.member.SocialAccount;
import com.grape.ticketing.repository.MemberRepository;
import com.grape.ticketing.repository.SocialAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
//구글 로그인용 -> 구글은 OidcUserService 탐
public class CustomOidcUserService extends OidcUserService {

    private final MemberRepository memberRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);

        String providerId = oidcUser.getSubject(); // sub
        String email = oidcUser.getEmail();
        String name = oidcUser.getFullName();
        System.out.println("CustomOidcUserService 실행됨");
        System.out.println("email = " + oidcUser.getEmail());
        System.out.println("sub = " + oidcUser.getSubject());

        SocialAccount socialAccount = socialAccountRepository
                .findByProviderAndProviderId(AuthProvider.GOOGLE, providerId)
                .orElseGet(() -> createGoogleAccount(providerId, email, name));

        Member member = socialAccount.getMember();


        return new DefaultOidcUser(
                List.of(new SimpleGrantedAuthority(member.getRole())),
                oidcUser.getIdToken(),
                oidcUser.getUserInfo(),
                "sub"
        );
    }

    private SocialAccount createGoogleAccount(String providerId, String email, String name) {
        String username = "google_" + providerId;
        String encodedPassword = passwordEncoder.encode(UUID.randomUUID().toString());

        Member member = new Member(
                name,
                email,
                username,
                encodedPassword,
                "ROLE_USER"
        );

        Member savedMember = memberRepository.save(member);

        SocialAccount socialAccount = SocialAccount.builder()
                .provider(AuthProvider.GOOGLE)
                .providerId(providerId)
                .email(email)
                .member(savedMember)
                .build();

        return socialAccountRepository.save(socialAccount);
    }
}