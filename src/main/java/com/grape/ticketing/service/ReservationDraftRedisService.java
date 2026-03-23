package com.grape.ticketing.service;

import com.grape.ticketing.domain.Member;
import com.grape.ticketing.domain.Performance;
import com.grape.ticketing.domain.Reservation;
import com.grape.ticketing.domain.ReservationSeat;
import com.grape.ticketing.domain.Seat;
import com.grape.ticketing.domain.status.SeatStatus;
import com.grape.ticketing.dto.ReservationConfirmResponse;
import com.grape.ticketing.dto.ReservationDraftCacheDto;
import com.grape.ticketing.dto.ReservationDraftCreateRequest;
import com.grape.ticketing.dto.ReservationDraftResponse;
import com.grape.ticketing.dto.ReservationDraftUpdateRequest;
import com.grape.ticketing.repository.MemberRepository;
import com.grape.ticketing.repository.PerformanceRepository;
import com.grape.ticketing.repository.ReservationRepository;
import com.grape.ticketing.repository.ReservationSeatRepository;
import com.grape.ticketing.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationDraftRedisService {

    private static final String KEY_PREFIX = "reservation:draft:";
    private static final Duration TTL = Duration.ofMinutes(30);

    private final RedisTemplate<String, Object> redisTemplate;
    private final MemberRepository memberRepository;
    private final PerformanceRepository performanceRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final SeatRepository seatRepository;

    public ReservationDraftResponse createDraft(ReservationDraftCreateRequest request) {
        ReservationDraftCacheDto draft = new ReservationDraftCacheDto();
        draft.setDraftId(UUID.randomUUID());
        draft.setMemberId(request.getMemberId());
        draft.setPerformanceId(request.getPerformanceId());
        draft.setPerformanceDate(request.getPerformanceDate());
        draft.setPerformanceTitle(request.getPerformanceTitle());
        draft.setPerformancePrice(request.getPerformancePrice());
        draft.setPerformanceVenue(request.getPerformanceVenue());
        draft.setRemainingSeatLimit(request.getRemainingSeatLimit());
        draft.setPerformanceUrl(request.getPerformanceUrl());
        draft.setConfirmed(false);

        writeDraft(draft);
        log.info("Draft created. draftId={}, performanceId={}, memberId={}", draft.getDraftId(), draft.getPerformanceId(), draft.getMemberId());
        return toResponse(draft);
    }

    public ReservationDraftResponse getDraft(UUID draftId) {
        ReservationDraftCacheDto draft = getDraftEntity(draftId);
        log.info("Draft loaded. draftId={}, selectedSeats={}, totalPrice={}", draftId, draft.getSelectedSeats(), draft.getTotalPrice());
        return toResponse(draft);
    }

    public ReservationDraftResponse updateDraft(UUID draftId, ReservationDraftUpdateRequest request) {
        ReservationDraftCacheDto draft = getDraftEntity(draftId);

        if (request.getPerformanceDate() != null) draft.setPerformanceDate(request.getPerformanceDate());
        if (request.getPerformanceTitle() != null) draft.setPerformanceTitle(request.getPerformanceTitle());
        if (request.getPerformancePrice() != null) draft.setPerformancePrice(request.getPerformancePrice());
        if (request.getPerformanceVenue() != null) draft.setPerformanceVenue(request.getPerformanceVenue());
        if (request.getRemainingSeatLimit() != null) draft.setRemainingSeatLimit(request.getRemainingSeatLimit());
        if (request.getPerformanceUrl() != null) draft.setPerformanceUrl(request.getPerformanceUrl());
        if (request.getSelectedSeats() != null) draft.setSelectedSeats(request.getSelectedSeats());
        if (request.getTotalPrice() != null) draft.setTotalPrice(request.getTotalPrice());

        writeDraft(draft);
        log.info("Draft updated. draftId={}, selectedSeats={}, totalPrice={}", draftId, draft.getSelectedSeats(), draft.getTotalPrice());
        return toResponse(draft);
    }

    public ReservationDraftCacheDto getDraftEntity(UUID draftId) {
        Object value = redisTemplate.opsForValue().get(generateKey(draftId));
        if (value == null) {
            throw new IllegalArgumentException("임시 예매 정보가 없습니다.");
        }
        return (ReservationDraftCacheDto) value;
    }

    @Transactional
    public ReservationConfirmResponse confirmDraft(UUID draftId) {
        ReservationDraftCacheDto draft = getDraftEntity(draftId);

        Member member = memberRepository.findById(draft.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("회원이 없습니다."));
        Performance performance = performanceRepository.findById(draft.getPerformanceId())
                .orElseThrow(() -> new IllegalArgumentException("공연이 없습니다."));

        List<String> selectedSeats = draft.getSelectedSeats();
        if (selectedSeats == null || selectedSeats.isEmpty()) {
            throw new IllegalArgumentException("선택된 좌석이 없습니다.");
        }

        Reservation reservation = new Reservation();
        reservation.setMember(member);
        reservation.setPerformance(performance);
        reservation.setReservedAt(LocalDateTime.now());
        reservation.setReservationStatus("RESERVED");
        Reservation savedReservation = reservationRepository.save(reservation);

        for (String seatNumber : selectedSeats) {
            Seat seat = seatRepository.findByPerformanceIdAndSeatNumber(draft.getPerformanceId(), seatNumber)
                    .orElseThrow(() -> new IllegalArgumentException("좌석이 없습니다: " + seatNumber));
            seat.setSeatStatus(SeatStatus.RESERVED);

            ReservationSeat reservationSeat = new ReservationSeat();
            reservationSeat.setReservation(savedReservation);
            reservationSeat.setSeat(seat);
            reservationSeatRepository.save(reservationSeat);
        }

        redisTemplate.delete(generateKey(draftId));
        log.info("Draft confirmed and removed. draftId={}, reservationId={}", draftId, savedReservation.getId());
        return new ReservationConfirmResponse(savedReservation.getId(), "최종 예매가 완료되었습니다.");
    }

    private void writeDraft(ReservationDraftCacheDto draft) {
        redisTemplate.opsForValue().set(generateKey(draft.getDraftId()), draft, TTL);
    }

    private String generateKey(UUID draftId) {
        return KEY_PREFIX + draftId;
    }

    private ReservationDraftResponse toResponse(ReservationDraftCacheDto draft) {
        return new ReservationDraftResponse(
                draft.getDraftId(),
                draft.getMemberId(),
                draft.getPerformanceId(),
                draft.getPerformanceDate(),
                draft.getPerformanceTitle(),
                draft.getPerformancePrice(),
                draft.getPerformanceVenue(),
                draft.getRemainingSeatLimit(),
                draft.getPerformanceUrl(),
                draft.getSelectedSeats(),
                draft.getTotalPrice(),
                draft.isConfirmed()
        );
    }
}
