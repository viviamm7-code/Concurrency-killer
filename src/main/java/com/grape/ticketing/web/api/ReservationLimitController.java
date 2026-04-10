package com.grape.ticketing.web.api;

import com.grape.ticketing.dto.reservation.RemainingSeatCountTO;
import com.grape.ticketing.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/performances")
@RequiredArgsConstructor
@Tag(name = "예매 로직 관리 API")
public class ReservationLimitController {

    private final ReservationService reservationService;

    private Long getLoginMemberId(HttpSession session) {
        Long memberId = (Long) session.getAttribute("loginMember");
        if (memberId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return memberId;
    }

    @Operation(summary = "공연 예매 가능 잔여 좌석 수 조회")
    @GetMapping("/{performanceId}/remaining-seat-count")
    public RemainingSeatCountTO getRemainingSeatCount(
            @PathVariable Long performanceId,
            HttpSession session
    ) {
        long remainingSeatCount =
                reservationService.getRemainingSeatCount(performanceId, getLoginMemberId(session));

        return new RemainingSeatCountTO(remainingSeatCount);
    }
}