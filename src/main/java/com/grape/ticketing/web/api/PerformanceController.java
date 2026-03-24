package com.grape.ticketing.web.api;
import java.util.List;
import com.grape.ticketing.dto.performance.PerformanceSummaryResponse;
import com.grape.ticketing.service.ReservationService;
import com.grape.ticketing.dto.performance.PerformanceTO;
import com.grape.ticketing.repository.MemberRepository;
import com.grape.ticketing.service.PerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/performances")
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
        private final MemberRepository memberRepository;

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



