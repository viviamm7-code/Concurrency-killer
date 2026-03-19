package com.grape.ticketing.web.api;

import com.grape.ticketing.dto.ReservationDetailDto;
import com.grape.ticketing.dto.ReservationDto;
import com.grape.ticketing.service.ReservationService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReservationApiController {
    private final ReservationService reservationService;

    @GetMapping("/api/reservation")
    public List<ReservationDto> reservationList() {
        Long id = 1L;
        return reservationService.getReservationList(id);
    }

    @GetMapping("/api/reservation/{reservationId}/detail")
    public ReservationDetailDto detailReservation(@PathVariable("reservationId") Long reservationId) {
        Long id = 1L;
        return reservationService.getDetailReservation(id,reservationId);
    }
}
