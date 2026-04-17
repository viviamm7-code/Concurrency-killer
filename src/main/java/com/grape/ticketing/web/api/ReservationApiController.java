package com.grape.ticketing.web.api;

import com.grape.ticketing.domain.member.Member;
import com.grape.ticketing.dto.reservation.ReservationCancelDto;
import com.grape.ticketing.dto.reservation.ReservationDetailDto;
import com.grape.ticketing.dto.reservation.ReservationDto;
import com.grape.ticketing.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "내 예매 페이지 API")
public class ReservationApiController {

    private final ReservationService reservationService;

    @Operation(summary = "내 예매 목록 조회")
    @GetMapping("/api/reservation")
    public List<ReservationDto> reservationList(@AuthenticationPrincipal Member member) {
//        System.out.println("session.getId() = " + session.getId());
//        System.out.println(session.getAttribute("loginMember"));
        return reservationService.getReservationList(member.getId());
    }

    @Operation(summary = "내 예매 상세 목록 조회")
    @GetMapping("/api/reservation/{reservationId}/detail")
    public ReservationDetailDto detailReservation(@PathVariable Long reservationId,
                                                  @AuthenticationPrincipal Member member) {
        return reservationService.getDetailReservation(member.getId(), reservationId);
    }

    @Operation(summary = "내 예매 환불")
    @PostMapping("/api/reservation/{reservationId}/cancel")
    public ReservationCancelDto cancelReservation(@PathVariable Long reservationId,
                                                  @AuthenticationPrincipal Member member) {
        return reservationService.cancelReservation(member.getId(), reservationId);
    }

    @Operation(summary = "내 예매 환불 미리보기")
    @GetMapping("/api/reservation/{reservationId}/cancel-preview")
    public ReservationCancelDto cancelPreview(@PathVariable Long reservationId,
                                              @AuthenticationPrincipal Member member) {
        return reservationService.getCancelPreview(member.getId(), reservationId);
    }

    @GetMapping("/api/reservation/{reservationId}/confirm")
    public ReservationDetailDto reservationConfirm(@PathVariable Long reservationId,
                                                   @AuthenticationPrincipal Member member) {
        return reservationService.getDetailReservation(member.getId(), reservationId);
    }
}