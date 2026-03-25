package com.grape.ticketing.web.api;

import com.grape.ticketing.dto.reservation.ReservationCancelDto;
import com.grape.ticketing.dto.reservation.ReservationDetailDto;
import com.grape.ticketing.dto.reservation.ReservationDto;
import com.grape.ticketing.service.ReservationService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReservationApiController {

    private final ReservationService reservationService;

    private Long getLoginMemberId(HttpSession session) {
        Long memberId = (Long) session.getAttribute("loginMember");
        if (memberId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        return memberId;
    }

    @GetMapping("/api/reservation")
    public List<ReservationDto> reservationList(HttpSession session) {
        System.out.println("session.getId() = " + session.getId());
        System.out.println(session.getAttribute("loginMember"));
        return reservationService.getReservationList(getLoginMemberId(session));
    }

    @GetMapping("/api/reservation/{reservationId}/detail")
    public ReservationDetailDto detailReservation(@PathVariable Long reservationId,
                                                  HttpSession session) {
        return reservationService.getDetailReservation(getLoginMemberId(session), reservationId);
    }

    @PostMapping("/api/reservation/{reservationId}/cancel")
    public ReservationCancelDto cancelReservation(@PathVariable Long reservationId,
                                                  HttpSession session) {
        return reservationService.cancelReservation(getLoginMemberId(session), reservationId);
    }

    @GetMapping("/api/reservation/{reservationId}/cancel-preview")
    public ReservationCancelDto cancelPreview(@PathVariable Long reservationId,
                                              HttpSession session) {
        return reservationService.getCancelPreview(getLoginMemberId(session), reservationId);
    }

    @GetMapping("/api/reservation/{reservationId}/confirm")
    public ReservationDetailDto reservationConfirm(@PathVariable Long reservationId,
                                                   HttpSession session) {
        return reservationService.getDetailReservation(getLoginMemberId(session), reservationId);
    }
}