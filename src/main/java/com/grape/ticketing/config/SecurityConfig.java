package com.grape.ticketing.config;

import com.grape.ticketing.domain.member.Member;
import com.grape.ticketing.repository.MemberRepository;
import com.grape.ticketing.service.CustomOAuth2UserService;
import com.grape.ticketing.service.CustomOidcUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

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
                //비로그인
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/join", "/login", "/css/**", "/js/**", "/images" +
                                        "/**", "/api" +
                                        "/auth/check"
                        , "/performance-list", "/already-logged-in",
                                //api도 다 가져오기
                                "/api/**"
                                ,"/performances/**", "/error")
                        .permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                //일반 로그인
                .formLogin(form -> form
                        .loginPage("/login")
                        // 여기서 Spring Security가 CustomUserDetailService 호출
                        .loginProcessingUrl("/members/login")
                        .defaultSuccessUrl("/performance-list", false)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                //소셜 로그인
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .authorizationEndpoint(endpoint -> endpoint
                                .authorizationRequestResolver(authorizationRequestResolver)
                        )
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(customOidcUserService)
                        )
                        .successHandler((request, response, authentication) -> {
                            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
                            String email = (String) oAuth2User.getAttributes().get("email");

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
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }
}