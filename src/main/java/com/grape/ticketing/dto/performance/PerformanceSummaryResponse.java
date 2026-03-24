package com.grape.ticketing.dto.performance;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PerformanceSummaryResponse {
    private Long performanceId;
    private String performanceDate;
    private String performanceTitle;
    private Integer performancePrice;
    private String performanceVenue;
    private Integer remainingSeatLimit;
    private String performanceUrl;
}
