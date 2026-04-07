package com.grape.ticketing.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyPageResponseDto {
    private Long memberId;
    private String username;
    private String name;
    private String email;
    private String role;
    private String createdAt;
}