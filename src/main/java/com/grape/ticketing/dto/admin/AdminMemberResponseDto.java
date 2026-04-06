package com.grape.ticketing.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminMemberResponseDto {
    private Long memberId;
    private String loginId;
    private String role;
}