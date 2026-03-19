package com.grape.ticketing.web;

import com.grape.ticketing.dto.RemainingSeatCountTO;
import com.grape.ticketing.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/performances")
//예매하는 과정에 필요한 로직들 관리하는 컨트롤러
public class ReservationLimitController {
    @Autowired
    private ReservationService reservationService;

    @GetMapping("/{performanceId}/remaining-seat-count")
    public RemainingSeatCountTO getRemainingSeatCount(
            @PathVariable Long performanceId,
            @RequestParam Long memberId
    ) {
        long remainingSeatCount = reservationService.getRemainingSeatCount(memberId, performanceId);

        return new RemainingSeatCountTO(remainingSeatCount);
    }
}
