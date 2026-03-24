package com.grape.ticketing.web.nav;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PerformancePageController {

    @GetMapping("/performance-list")
    public String performanceListPage() {
        return "performance-list";
    }

    @GetMapping("/performances/{performanceId}")
    public String performanceDetailPage(@PathVariable Long performanceId) {
        return "performance";
    }

    @GetMapping("/performance2")
    public String performance2Page() {
        return "performance2";
    }

    @GetMapping("/seat")
    public String seatPage() {
        return "seat";
    }

    @GetMapping("/reservationConfirm")
    public String reservationConfirmPage() {
        return "reservationConfirm";
    }
}
