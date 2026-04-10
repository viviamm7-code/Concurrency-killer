package com.grape.ticketing.web.api;
import java.util.List;
import java.util.Map;

import com.grape.ticketing.dto.performance.PerformanceSummaryResponse;
import com.grape.ticketing.service.ReservationService;
import com.grape.ticketing.dto.performance.PerformanceTO;
import com.grape.ticketing.repository.MemberRepository;
import com.grape.ticketing.service.PerformanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/performances")
@RequiredArgsConstructor
@Tag(name = "공연 페이지 API")
public class PerformanceController {

    private final PerformanceService performanceService;
    private final ReservationService reservationService;
    @Value("${kakao.map.app-key}")
    private String kakaoMapAppKey;

    @Operation(summary = "draft에 저장될 공연 상세 정보 조회")
    @GetMapping("/{performanceId}/summary")
    public PerformanceSummaryResponse getPerformanceSummary(
            @PathVariable Long performanceId,
            @RequestParam Long memberId
    ) {
        long remainingSeatCount = reservationService.getRemainingSeatCount(memberId, performanceId);
        return performanceService.getPerformanceSummary(performanceId, remainingSeatCount);
    }

    @Operation(summary = "공연 목록 조회")
    @GetMapping
    public List<PerformanceTO.PerformanceList> getPerformanceList() {
        return performanceService.getPerformanceList();
    }

    @Operation(summary = "공연 별 상세 목록 조회")
    @GetMapping("/{performanceId}")
    public PerformanceTO.PerformanceRes getPerformance(@PathVariable Long performanceId) {
        return performanceService.getPerformance(performanceId);
    }

    @Operation(summary = "공연장 지도 KaKaoMap API Key 조회")
    @GetMapping("/config/map")
    public Map<String, String> getMapConfig() {
        return Map.of("kakaoMapAppKey", kakaoMapAppKey);
    }
}



