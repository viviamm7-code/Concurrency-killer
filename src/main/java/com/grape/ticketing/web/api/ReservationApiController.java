package com.grape.ticketing.web.api;

import com.grape.ticketing.dto.ReservationCancelDto;
import com.grape.ticketing.dto.ReservationConfirmRequest;
import com.grape.ticketing.dto.ReservationDetailDto;
import com.grape.ticketing.dto.ReservationDto;
import com.grape.ticketing.service.ReservationService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    // 좌석에서 받기 전 임시 api
    @GetMapping("/api/reservation/{reservationId}/confirm")
    public ReservationDetailDto ReservationConfirm(@PathVariable("reservationId") Long reservationId) {
        Long id = 2L;
        return reservationService.getDetailReservation(id, reservationId);
    }

    @PostMapping("/api/reservation/confirm")
    public ResponseEntity<Map<String, Long>> confirmReservation(@RequestBody ReservationConfirmRequest request) {
        Long reservationId = reservationService.confirmReservation(request);

        return ResponseEntity.ok(Map.of("reservationId", reservationId));
    }
}
