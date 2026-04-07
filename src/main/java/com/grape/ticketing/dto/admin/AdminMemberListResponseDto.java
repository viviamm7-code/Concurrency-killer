package com.grape.ticketing.dto.admin;


import com.grape.ticketing.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AdminMemberListResponseDto {

    private Long id;
    private String username;
}