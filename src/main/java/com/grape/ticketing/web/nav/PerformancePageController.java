package com.grape.ticketing.web.nav;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PerformancePageController {

    @GetMapping("/performance-list")
    public String performanceListPage() {
        return "performance-list";
    }
}
