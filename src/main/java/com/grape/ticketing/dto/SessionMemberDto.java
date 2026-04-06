package com.grape.ticketing.dto;

import com.grape.ticketing.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SessionMemberDto {
    private Long id;
    private String username;
    private String role;

    public static SessionMemberDto from(Member member) {
        return new SessionMemberDto(
                member.getId(),
                member.getUsername(),
                member.getRole()
        );
    }
}