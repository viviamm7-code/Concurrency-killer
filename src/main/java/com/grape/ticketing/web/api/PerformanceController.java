package com.grape.ticketing.web.api;

import com.grape.ticketing.dto.PerformanceTO;
import com.grape.ticketing.repository.MemberRepository;
import com.grape.ticketing.service.PerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/performances")
@RequiredArgsConstructor
public class PerformanceController {
    private final PerformanceService performanceService;

    //공연 목록 조회
    @GetMapping
    public List<PerformanceTO.PerformanceList> getPerformanceList() {
        return performanceService.getPerformanceList();
    }

    //공연 상세 조회
    @GetMapping("/{performanceId}")
    public PerformanceTO.PerformanceRes getPerformance(@PathVariable Long performanceId) {
        return performanceService.getPerformance(performanceId);
    }
}
