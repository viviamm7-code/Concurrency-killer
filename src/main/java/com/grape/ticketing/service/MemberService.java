package com.grape.ticketing.service;

import com.grape.ticketing.domain.member.Member;
import com.grape.ticketing.dto.member.MemberTO;
import com.grape.ticketing.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void join(MemberTO memberTO) {
        // 아이디 중복 검사
        if (memberRepository.existsByUsername(memberTO.getUsername())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        // 비밀번호 암호화 및 엔티티 변환
        Member member = Member.builder()
                .username(memberTO.getUsername())
                .password(passwordEncoder.encode(memberTO.getPassword()))
                .name(memberTO.getName())
                .email(memberTO.getEmail())
                .role("ROLE_USER")
                .build();

        memberRepository.save(member);
    }
}
