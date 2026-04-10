package com.grape.ticketing.web.api;

import com.grape.ticketing.domain.member.Member;
import com.grape.ticketing.dto.reservation.ReservationConfirmResponse;
import com.grape.ticketing.dto.reservation.ReservationDraftCreateRequest;
import com.grape.ticketing.dto.reservation.ReservationDraftResponse;
import com.grape.ticketing.dto.reservation.ReservationDraftUpdateRequest;
import com.grape.ticketing.exception.SeatHoldConflictException;
import com.grape.ticketing.service.ReservationDraftRedisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
@Tag(name = "Redis 정보 저장")
@RestController
@RequestMapping("reservation-drafts")
@RequiredArgsConstructor
@Tag(name = "Redis 사용 API")
public class ReservationDraftController {

    private final ReservationDraftRedisService reservationDraftRedisService;

    @Operation(summary = "임시 예매 정보 저장")
    @PostMapping
    public ReservationDraftResponse createDraft(
            @RequestBody ReservationDraftCreateRequest request,
            HttpSession session
    ) {
        Long memberId = (Long) session.getAttribute("loginMember");

        if (memberId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        return reservationDraftRedisService.createDraft(memberId, request);
    }

    @Operation(summary = "임시 예매 정보 조회")
    @GetMapping("/{draftId}")
    public ReservationDraftResponse getDraft(@PathVariable UUID draftId) {
        return reservationDraftRedisService.getDraft(draftId);
    }

    @Operation(summary = "임시 예매 정보 수정")
    @PutMapping("/{draftId}")
    public ReservationDraftResponse updateDraft(
            @PathVariable UUID draftId,
            @RequestBody ReservationDraftUpdateRequest request
    ) {
        return reservationDraftRedisService.updateDraft(draftId, request);
    }

    @Operation(summary = "예매 완료 저장")
    @PostMapping("/{draftId}/confirm")
    public ReservationConfirmResponse confirmDraft(@PathVariable UUID draftId) {
        return reservationDraftRedisService.confirmDraft(draftId);
    }

    @Operation(summary = "선점된 좌석 풀기")
    @PostMapping("/{draftId}/release-seats")
    public ReservationDraftResponse releaseSelectedSeats(@PathVariable UUID draftId) {
        return reservationDraftRedisService.releaseSelectedSeats(draftId);
    }

    @ExceptionHandler(SeatHoldConflictException.class)
    public ResponseEntity<Map<String, Object>> handleSeatHoldConflict(SeatHoldConflictException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", "이미 다른 사용자가 선점한 좌석이 있습니다.");
        body.put("conflictedSeats", ex.getConflictedSeats());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }
}
