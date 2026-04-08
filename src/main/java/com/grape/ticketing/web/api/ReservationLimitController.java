package com.grape.ticketing.web.api;

import com.grape.ticketing.domain.member.Member;
import com.grape.ticketing.dto.reservation.RemainingSeatCountTO;
import com.grape.ticketing.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("performances")
@RequiredArgsConstructor
//예매하는 과정에 필요한 로직들 관리하는 컨트롤러
@Tag(name = "예매 로직 관리 API")
public class ReservationLimitController {

    private final ReservationService reservationService;

    @Operation(summary = "공연 예매 가능 잔여 좌석 수 조회")
    @GetMapping("/{performanceId}/remaining-seat-count")
    public RemainingSeatCountTO getRemainingSeatCount(
            @PathVariable Long performanceId,
            @AuthenticationPrincipal Member member
    ) {
        long remainingSeatCount = reservationService.getRemainingSeatCount(performanceId, member.getId());

        return new RemainingSeatCountTO(remainingSeatCount);
    }
}
