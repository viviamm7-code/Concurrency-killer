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
                //비로그인
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/css/**", "/js/**", "/images/**", "/api/auth/check"
                        , "/performance-list",
                                //api도 다 가져오기
                                "/api/**"
                                ,"/performances/**")
                        .permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                //일반 로그인
                .formLogin(form -> form
                        .loginPage("/login")
                        // 여기서 Spring Security가 CustomUserDetailService 호출
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
                //소셜 로그인
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .authorizationEndpoint(endpoint -> endpoint
                                .authorizationRequestResolver(authorizationRequestResolver)
                        )
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)   // 네이버로
                                .oidcUserService(customOidcUserService) // 구글로
                        )
                        .successHandler((request, response, authentication) -> {
                            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

                            String email = (String) oAuth2User.getAttributes().get("email");

                            if (email == null) {
                                Object responseObj = oAuth2User.getAttributes().get("response");
                                if (responseObj instanceof Map<?, ?> responseMap) {
                                    email = (String) responseMap.get("email");
                                }
                            }

                            if (email == null) {
                                throw new IllegalStateException("소셜 로그인 email이 없습니다.");
                            }

                            final String finalEmail = email;

                            Member member = memberRepository.findByEmail(finalEmail)
                                    .orElseThrow(() -> new IllegalArgumentException("소셜 로그인 회원이 없습니다. email=" + finalEmail));

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