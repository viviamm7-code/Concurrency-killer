package com.grape.ticketing.web.nav;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PerformancePageController {

    //공연 목록 화면
    @GetMapping("/performance-list")
    public String performanceListPage() {
        return "performance-list";
    }

    //공연 상세 화면
    @GetMapping("/performances/{performanceId}")
    public String performanceDetailPage(@PathVariable Long performanceId) {
        return "performance";
    }
}
