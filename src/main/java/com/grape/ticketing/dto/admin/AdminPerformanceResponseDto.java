package com.grape.ticketing.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminPerformanceResponseDto {
    private Long performanceId;
    private String title;
    private String venue;
    private String performanceDate;
}