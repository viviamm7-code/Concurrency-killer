package com.grape.ticketing.config;

import com.grape.ticketing.domain.member.Member;
import com.grape.ticketing.repository.MemberRepository;
import com.grape.ticketing.service.CustomOAuth2UserService;
import com.grape.ticketing.service.CustomOidcUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final MemberRepository memberRepository;
    private final CustomOidcUserService customOidcUserService;
    private final OAuth2AuthorizationRequestResolver authorizationRequestResolver;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**", "/api/admin/**")
                        .hasAnyAuthority("ROLE_ADMIN")

                        .requestMatchers("/api/me", "/api/mypage")
                        .authenticated()

                        .requestMatchers(
                                "/", "/login", "/signup",
                                "/css/**", "/js/**", "/images/**",
                                "/api/auth/check", "/api/auth/status",
                                "/performance-list", "/performances/**", "/api/performances/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/members/login")
                        .successHandler((request, response, authentication) -> {
                            System.out.println("authentication name = " + authentication.getName());

                            Member member = memberRepository.findByUsername(authentication.getName())
                                    .orElseThrow();

                            request.getSession().setAttribute("loginMember", member.getId());
                            response.sendRedirect("/performance-list");
                        })
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .authorizationEndpoint(endpoint -> endpoint
                                .authorizationRequestResolver(authorizationRequestResolver)
                        )
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                                .oidcUserService(customOidcUserService)
                        )
                        .successHandler((request, response, authentication) -> {
                            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

                            String email = (String) oAuth2User.getAttributes().get("email");

                            if (email == null) {
                                throw new IllegalStateException("소셜 로그인 email이 없습니다.");
                            }

                            Member member = memberRepository.findByEmail(email)
                                    .orElseThrow(() -> new IllegalArgumentException("소셜 로그인 회원이 없습니다. email=" + email));

                            request.getSession().setAttribute("loginMember", member.getId());
                            response.sendRedirect("/performance-list");
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }

    private String extractEmail(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 1. 구글
        String email = (String) attributes.get("email");
        if (email != null) {
            return email;
        }

        // 2. 네이버
        Object responseObj = attributes.get("response");
        if (responseObj instanceof Map<?, ?> responseMap) {
            Object naverEmail = responseMap.get("email");
            if (naverEmail instanceof String) {
                return (String) naverEmail;
            }
        }

        // 3. 카카오
        Object kakaoAccountObj = attributes.get("kakao_account");
        if (kakaoAccountObj instanceof Map<?, ?> kakaoAccountMap) {
            Object kakaoEmail = kakaoAccountMap.get("email");
            if (kakaoEmail instanceof String) {
                return (String) kakaoEmail;
            }
        }

        return null;
    }
}