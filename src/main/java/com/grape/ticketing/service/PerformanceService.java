package com.grape.ticketing.service;

import com.grape.ticketing.domain.Performance;
import com.grape.ticketing.dto.PerformanceSummaryResponse;
import com.grape.ticketing.repository.PerformanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerformanceService {
    private final PerformanceRepository performanceRepository;

    public PerformanceSummaryResponse getPerformanceSummary(Long performanceId, Long remainingSeatLimit) {
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> new IllegalArgumentException("공연이 없습니다."));

        return new PerformanceSummaryResponse(
                performance.getId(),
                performance.getStartedAt() == null ? "" : performance.getStartedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                performance.getPerformanceName(),
                performance.getPrice(),
                performance.getVenue(),
                remainingSeatLimit == null ? 0 : remainingSeatLimit.intValue(),
                "/performances/" + performance.getId()
        );
    }
}
