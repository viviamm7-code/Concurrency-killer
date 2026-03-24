package com.grape.ticketing.web.api;

import com.grape.ticketing.dto.reservation.ReservationConfirmResponse;
import com.grape.ticketing.dto.reservation.ReservationDraftCreateRequest;
import com.grape.ticketing.dto.reservation.ReservationDraftResponse;
import com.grape.ticketing.dto.reservation.ReservationDraftUpdateRequest;
import com.grape.ticketing.service.ReservationDraftRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
