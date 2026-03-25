package com.grape.ticketing.web.nav;

import com.grape.ticketing.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @GetMapping("reservation")
    public String reservationList() {
        return "reservation/reservationList";
    }

    @GetMapping("reservation/{reservationId}")
    public String nextReservation() {
        return "reservation/detailReservation";
    }

    @GetMapping("reservation/{reservationId}/confirm")
    public String ReservationCheck() {
        return "reservation/reservationConfirm2";
    }
}
