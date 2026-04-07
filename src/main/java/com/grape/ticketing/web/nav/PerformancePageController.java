package com.grape.ticketing.web.nav;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PerformancePageController {

    @Value("${toss.client-key}")
    private String tossClientKey;
    @Value("${kakao.map.app-key}")
    private String kakaoMapAppKey;

    @GetMapping("/performance-list")
    public String performanceListPage() {
        return "/performances/performance-list";
    }

    @GetMapping("/performances/{performanceId}")
    public String performanceDetailPage(@PathVariable Long performanceId, Model model) {
        model.addAttribute("kakaoMapAppKey", kakaoMapAppKey);
        return "/performances/performance";
    }

    @GetMapping("/seat")
    public String seatPage() {
        return "/seats/seat";
    }

    @GetMapping("/reservationConfirm")
    public String reservationConfirmPage() {
        return "/reservation/reservationConfirm";
    }

    @GetMapping("/reservationConfirm2")
    public String reservationConfirm2Page(Model model) {
        model.addAttribute("tossClientKey", tossClientKey);
        return "reservation/reservationConfirm2";
    }
}
