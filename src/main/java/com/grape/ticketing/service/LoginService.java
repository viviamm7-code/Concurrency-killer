package com.grape.ticketing.service;

import com.grape.ticketing.domain.member.Member;
import com.grape.ticketing.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final MemberRepository memberRepository;

    public Member login(String username, String password) {
        return memberRepository.findByUsername(username)
                .filter(member -> member.getPassword().equals(password))
                .orElse(null);
    }
}