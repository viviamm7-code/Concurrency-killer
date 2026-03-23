package com.grape.ticketing.web.api;

import com.grape.ticketing.dto.PerformanceSummaryResponse;
import com.grape.ticketing.service.PerformanceService;
import com.grape.ticketing.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/performances")
@RequiredArgsConstructor
public class PerformanceController {

    private final PerformanceService performanceService;
    private final ReservationService reservationService;

    @GetMapping("/{performanceId}/summary")
    public PerformanceSummaryResponse getPerformanceSummary(
            @PathVariable Long performanceId,
            @RequestParam Long memberId
    ) {
        long remainingSeatCount = reservationService.getRemainingSeatCount(memberId, performanceId);
        return performanceService.getPerformanceSummary(performanceId, remainingSeatCount);
    }
}
