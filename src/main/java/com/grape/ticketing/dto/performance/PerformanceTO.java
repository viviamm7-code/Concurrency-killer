package com.grape.ticketing.dto.performance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PerformanceRes {
        private Long id;
        private String imageUrl;
        private String performanceName;
        private String venue;
        private LocalDate startDate;
        private LocalDate endDate;
        private int performanceTime;
        private LocalDateTime startedAt;
        private int price;
        private Integer remainingSeatLimit;
        private String address;
        private Double latitude;
        private Double longitude;


    }
}
