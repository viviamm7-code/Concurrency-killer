package com.grape.ticketing.web.api;

import com.grape.ticketing.domain.member.Member;
import com.grape.ticketing.dto.reservation.RemainingSeatCountTO;
import com.grape.ticketing.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
            @AuthenticationPrincipal Member member
    ) {
        long remainingSeatCount = reservationService.getRemainingSeatCount(performanceId, member.getId());

        return new RemainingSeatCountTO(remainingSeatCount);
    }
}
