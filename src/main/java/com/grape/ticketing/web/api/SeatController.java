package com.grape.ticketing.web.api;

import com.grape.ticketing.dto.seat.SeatTO;
import com.grape.ticketing.service.SeatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("performances")
@Tag(name = "좌석 API")
public class SeatController {

    private final SeatService seatService;

    @Operation(summary = "공연 별 모든 좌석 조회")
    @GetMapping("/{performanceId}/seats")
    public List<SeatTO> getSeatByPerformanceId(@PathVariable Long performanceId) {
        return seatService.findAllSeats(performanceId);
    }
}
