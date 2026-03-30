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

    // 테스트용 더미 로직
//    public Member login(String username, String password) {
//        if ("test".equals(username) && "1234".equals(password)) {
//            Member testMember = new Member();
//            testMember.setUsername("test");
//            testMember.setPassword("1234");
//            return testMember;
//        }
//        return null;
//    }

}