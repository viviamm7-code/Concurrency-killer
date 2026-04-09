package com.grape.ticketing.web.api;

import com.grape.ticketing.dto.reservation.ReservationCancelDto;
import com.grape.ticketing.dto.reservation.ReservationDetailDto;
import com.grape.ticketing.dto.reservation.ReservationDto;
import com.grape.ticketing.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "내 예매 페이지 API")
public class ReservationApiController {

    private final ReservationService reservationService;

    private Long getLoginMemberId(HttpSession session) {
        Long memberId = (Long) session.getAttribute("loginMember");
        if (memberId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return memberId;
    }

    @Operation(summary = "내 예매 목록 조회")
    @GetMapping("api/reservation")
    public List<ReservationDto> reservationList(HttpSession session) {
        return reservationService.getReservationList(getLoginMemberId(session));
    }

    @Operation(summary = "내 예매 상세 목록 조회")
    @GetMapping("api/reservation/{reservationId}/detail")
    public ReservationDetailDto detailReservation(@PathVariable Long reservationId,
                                                  HttpSession session) {
        return reservationService.getDetailReservation(getLoginMemberId(session), reservationId);
    }

    @Operation(summary = "내 예매 환불")
    @PostMapping("api/reservation/{reservationId}/cancel")
    public ReservationCancelDto cancelReservation(@PathVariable Long reservationId,
                                                  HttpSession session) {
        return reservationService.cancelReservation(getLoginMemberId(session), reservationId);
    }

    @Operation(summary = "내 예매 환불 미리보기")
    @GetMapping("api/reservation/{reservationId}/cancel-preview")
    public ReservationCancelDto cancelPreview(@PathVariable Long reservationId,
                                              HttpSession session) {
        return reservationService.getCancelPreview(getLoginMemberId(session), reservationId);
    }

    @GetMapping("api/reservation/{reservationId}/confirm")
    public ReservationDetailDto reservationConfirm(@PathVariable Long reservationId,
                                                   HttpSession session) {
        return reservationService.getDetailReservation(getLoginMemberId(session), reservationId);
    }
}