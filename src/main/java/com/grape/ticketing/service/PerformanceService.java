package com.grape.ticketing.service;

import com.grape.ticketing.domain.Performance;
import com.grape.ticketing.dto.performance.PerformanceSummaryResponse;
import com.grape.ticketing.domain.mapper.PerformanceMapper;
import com.grape.ticketing.dto.performance.PerformanceTO;
import com.grape.ticketing.repository.PerformanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
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
                performance.getVenue().getVenueName(),
                remainingSeatLimit == null ? 0 : remainingSeatLimit.intValue(),
                "/performances/" + performance.getId()
        );}
    private final PerformanceMapper performanceMapper;

    //공연 목록 조회
    public List<PerformanceTO.PerformanceList> getPerformanceList() {
        List<PerformanceTO.PerformanceList> performanceList = performanceRepository.findAll().stream()
                .map(performanceMapper::toPerformanceListTO)
                .toList();
        return performanceList;
    }

    //공연 상세 조회
    public PerformanceTO.PerformanceRes getPerformance(Long performanceId) {
        Performance performance = performanceRepository.findById(performanceId).orElseThrow();
        return performanceMapper.toPerformanceTO(performance);
    }
}
