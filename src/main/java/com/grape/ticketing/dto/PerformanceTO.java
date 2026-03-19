package com.grape.ticketing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class PerformanceTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PerformanceList {
        private Long id;
        private String imageUrl;
        private String performanceName;
        private String venue;
        private LocalDate startDate;
        private LocalDate endDate;
    }
}
