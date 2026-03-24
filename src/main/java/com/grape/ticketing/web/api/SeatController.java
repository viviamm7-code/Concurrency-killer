package com.grape.ticketing.web.api;

import com.grape.ticketing.dto.seat.SeatTO;
import com.grape.ticketing.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/performances")
public class SeatController {
    @Autowired
    private SeatService seatService;

    @GetMapping("/{performanceId}/seats")
    public List<SeatTO> getSeatByPerformanceId(@PathVariable Long performanceId) {
        return seatService.findAllSeats(performanceId);
    }
}
