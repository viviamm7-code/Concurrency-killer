package com.grape.ticketing.web.api;

import com.grape.ticketing.dto.reservation.ReservationCancelDto;
import com.grape.ticketing.dto.reservation.ReservationDetailDto;
import com.grape.ticketing.dto.reservation.ReservationDto;
import com.grape.ticketing.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReservationApiController {
    private final ReservationService reservationService;

    @GetMapping("/api/reservation")
    public List<ReservationDto> reservationList() {
        Long id = 2L;
        return reservationService.getReservationList(id);
    }

    @GetMapping("/api/reservation/{reservationId}/detail")
    public ReservationDetailDto detailReservation(@PathVariable("reservationId") Long reservationId) {
        Long id = 2L;
        return reservationService.getDetailReservation(id,reservationId);
    }

    @PostMapping("/api/reservation/{reservationId}/cancel")
    public ReservationCancelDto cancelReservation(@PathVariable("reservationId") Long reservationId) {
        Long id = 2L;
        return reservationService.cancelReservation(id, reservationId);
    }

    @GetMapping("/api/reservation/{reservationId}/cancel-preview")
    public ReservationCancelDto cancelPreview(@PathVariable Long reservationId) {
        Long memberId = 2L;
        return reservationService.getCancelPreview(memberId, reservationId);
    }
}
