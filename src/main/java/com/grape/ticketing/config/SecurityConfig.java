package com.grape.ticketing.config;

import com.grape.ticketing.domain.member.Member;
import com.grape.ticketing.repository.MemberRepository;
import com.grape.ticketing.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final MemberRepository memberRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
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
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}