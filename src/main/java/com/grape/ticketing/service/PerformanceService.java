package com.grape.ticketing.service;

import com.grape.ticketing.domain.Performance;
import com.grape.ticketing.domain.mapper.PerformanceMapper;
import com.grape.ticketing.dto.PerformanceTO;
import com.grape.ticketing.repository.PerformanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
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
