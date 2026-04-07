package com.grape.ticketing.dto.admin;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AdminMemberDetailResponseDto {
    private Long id;
    private String username;
    private String name;
    private String email;
    private String role;
    private LocalDateTime createdAt;
}