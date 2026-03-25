package com.grape.ticketing.web.api;

import com.grape.ticketing.dto.reservation.ReservationConfirmResponse;
import com.grape.ticketing.dto.reservation.ReservationDraftCreateRequest;
import com.grape.ticketing.dto.reservation.ReservationDraftResponse;
import com.grape.ticketing.dto.reservation.ReservationDraftUpdateRequest;
import com.grape.ticketing.exception.SeatHoldConflictException;
import com.grape.ticketing.service.ReservationDraftRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/reservation-drafts")
@RequiredArgsConstructor
public class ReservationDraftController {

    private final ReservationDraftRedisService reservationDraftRedisService;

    @PostMapping
    public ReservationDraftResponse createDraft(@RequestBody ReservationDraftCreateRequest request) {
        return reservationDraftRedisService.createDraft(request);
    }

    @GetMapping("/{draftId}")
    public ReservationDraftResponse getDraft(@PathVariable UUID draftId) {
        return reservationDraftRedisService.getDraft(draftId);
    }

    @PutMapping("/{draftId}")
    public ReservationDraftResponse updateDraft(
            @PathVariable UUID draftId,
            @RequestBody ReservationDraftUpdateRequest request
    ) {
        return reservationDraftRedisService.updateDraft(draftId, request);
    }

    @PostMapping("/{draftId}/confirm")
    public ReservationConfirmResponse confirmDraft(@PathVariable UUID draftId) {
        return reservationDraftRedisService.confirmDraft(draftId);
    }

    @ExceptionHandler(SeatHoldConflictException.class)
    public ResponseEntity<Map<String, Object>> handleSeatHoldConflict(SeatHoldConflictException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", "이미 다른 사용자가 선점한 좌석이 있습니다.");
        body.put("conflictedSeats", ex.getConflictedSeats());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }
}
