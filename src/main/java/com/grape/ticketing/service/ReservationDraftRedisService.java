package com.grape.ticketing.service;

import com.grape.ticketing.domain.member.Member;
import com.grape.ticketing.domain.Performance;
import com.grape.ticketing.domain.Reservation;
import com.grape.ticketing.domain.ReservationSeat;
import com.grape.ticketing.domain.Seat;
import com.grape.ticketing.domain.status.ReservationStatus;
import com.grape.ticketing.domain.status.SeatStatus;
import com.grape.ticketing.dto.reservation.ReservationConfirmResponse;
import com.grape.ticketing.dto.reservation.ReservationDraftCacheDto;
import com.grape.ticketing.dto.reservation.ReservationDraftCreateRequest;
import com.grape.ticketing.dto.reservation.ReservationDraftResponse;
import com.grape.ticketing.dto.reservation.ReservationDraftUpdateRequest;
import com.grape.ticketing.exception.SeatHoldConflictException;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationDraftRedisService {

    private static final String KEY_PREFIX = "reservation:draft:";
    private static final String SEAT_HOLD_KEY_PREFIX = "seat:hold:";
    private static final Duration DRAFT_TTL = Duration.ofMinutes(30);
    private static final Duration SEAT_HOLD_TTL = Duration.ofMinutes(3);

    private final RedisTemplate<String, Object> redisTemplate;
    private final MemberRepository memberRepository;
    private final PerformanceRepository performanceRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final SeatRepository seatRepository;
    private final QueueRedisService queueRedisService;

    public ReservationDraftResponse createDraft(Long memberId, ReservationDraftCreateRequest request) {
        ReservationDraftCacheDto draft = new ReservationDraftCacheDto();
        draft.setDraftId(UUID.randomUUID());
        draft.setMemberId(memberId);
        draft.setPerformanceId(request.getPerformanceId());
        draft.setPerformanceDate(request.getPerformanceDate());
        draft.setPerformanceTitle(request.getPerformanceTitle());
        draft.setPerformancePrice(request.getPerformancePrice());
        draft.setPerformanceVenue(request.getPerformanceVenue());
        draft.setStartedAt(request.getStartedAt());
        draft.setRemainingSeatLimit(request.getRemainingSeatLimit());
        draft.setPerformanceUrl(request.getPerformanceUrl());
        draft.setImageUrl(request.getImageUrl());
        draft.setConfirmed(false);
        draft.setReservedDate(request.getReservedDate());

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
        if (request.getStartedAt() != null) draft.setStartedAt(request.getStartedAt());
        if (request.getRemainingSeatLimit() != null) draft.setRemainingSeatLimit(request.getRemainingSeatLimit());
        if (request.getPerformanceUrl() != null) draft.setPerformanceUrl(request.getPerformanceUrl());
        if (request.getTotalPrice() != null) draft.setTotalPrice(request.getTotalPrice());

        if (request.getSelectedSeats() != null) {
            synchronizeSeatHolds(draft, request.getSelectedSeats());
            draft.setSelectedSeats(new ArrayList<>(request.getSelectedSeats()));
        }
        if (request.getReservedDate() != null) draft.setReservedDate(request.getReservedDate());

        writeDraft(draft);
        log.info("Draft updated. draftId={}, selectedSeats={}, totalPrice={}", draftId, draft.getSelectedSeats(), draft.getTotalPrice());
        return toResponse(draft);
    }

    public ReservationDraftCacheDto getDraftEntity(UUID draftId) {
        Object value = redisTemplate.opsForValue().get(generateKey(draftId));
        System.out.println(" 드래프트 아이디 : " + draftId + " draft: " + value);
        if (value == null) {
            throw new IllegalArgumentException("임시 예매 정보가 없습니다.");
        }
        return (ReservationDraftCacheDto) value;
    }

    //reservation, reservation_seat, seat, payment 테이블을 동시에 업데이트 되어야하기때문에 @Transactional 사용
    @Transactional
    public ReservationConfirmResponse confirmDraft(UUID draftId) {
        ReservationDraftCacheDto draft = getDraftEntity(draftId);

        // 로그 추가
        System.out.println("DEBUG: PerformanceId = " + draft.getPerformanceId());
        System.out.println("DEBUG: MemberId = " + draft.getMemberId());

        Member member = memberRepository.findById(draft.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("회원이 없습니다."));
        Performance performance = performanceRepository.findById(draft.getPerformanceId())
                .orElseThrow(() -> new IllegalArgumentException("공연이 없습니다."));

        List<String> selectedSeats = draft.getSelectedSeats();
        if (selectedSeats == null || selectedSeats.isEmpty()) {
            throw new IllegalArgumentException("선택된 좌석이 없습니다.");
        }

        validateSeatHolds(draft.getDraftId(), draft.getPerformanceId(), selectedSeats);

        // 예매 데이터 저장
        Reservation reservation = new Reservation();
        reservation.setMember(member);
        reservation.setPerformance(performance);
        reservation.setReservedAt(LocalDateTime.now());
        reservation.setStartedAt(draft.getStartedAt());
        reservation.setReservationStatus(ReservationStatus.RESERVED);
        reservation.setReservedDate(draft.getReservedDate());
        Reservation savedReservation = reservationRepository.save(reservation);

        // 예매 좌석 데이터 저장
        for (String seatNumber : selectedSeats) {
            Seat seat = seatRepository.findByPerformanceIdAndSeatNumber(draft.getPerformanceId(), seatNumber)
                    .orElseThrow(() -> new IllegalArgumentException("좌석이 없습니다: " + seatNumber));
            seat.setSeatStatus(SeatStatus.RESERVED);
//            seatRepository.save(seat);
            System.out.println(seat.toString());


            ReservationSeat reservationSeat = new ReservationSeat();
            reservationSeat.setReservation(savedReservation);
            reservationSeat.setSeat(seat);
            reservationSeatRepository.save(reservationSeat);
        }

        releaseSeatHolds(draft.getDraftId(), draft.getPerformanceId(), selectedSeats);
        deleteActiveUser(draft.getMemberId(), draft.getPerformanceId());  //예매 확정이므로 대기열 입장 허용 사용자 삭제
        redisTemplate.delete(generateKey(draftId));
//        log.info("Draft confirmed and removed. draftId={}, reservationId={}", draftId, savedReservation.getId());
        return new ReservationConfirmResponse(savedReservation.getId(), "최종 예매가 완료되었습니다.");
    }

    @Transactional
    public ReservationDraftResponse releaseSelectedSeats(UUID draftId) {
        ReservationDraftCacheDto draft = getDraftEntity(draftId);

        List<String> selectedSeats = draft.getSelectedSeats();
        if (selectedSeats != null && !selectedSeats.isEmpty()) {
            releaseSeatHolds(draft.getDraftId(), draft.getPerformanceId(), selectedSeats);
        }

        draft.setSelectedSeats(new ArrayList<>());
        draft.setTotalPrice(0);

        redisTemplate.opsForValue().set(generateKey(draftId), draft, DRAFT_TTL);

        return toResponse(draft);
    }

    private void synchronizeSeatHolds(ReservationDraftCacheDto draft, List<String> requestedSeats) {
        List<String> currentSeats = draft.getSelectedSeats() == null
                ? new ArrayList<>()
                : new ArrayList<>(draft.getSelectedSeats());
        List<String> targetSeats = new ArrayList<>(requestedSeats);

        Set<String> currentSet = new HashSet<>(currentSeats);
        Set<String> targetSet = new HashSet<>(targetSeats);

        List<String> toRelease = currentSeats.stream()
                .filter(seatNumber -> !targetSet.contains(seatNumber))
                .toList();

        List<String> toAcquire = targetSeats.stream()
                .filter(seatNumber -> !currentSet.contains(seatNumber))
                .toList();

        List<String> acquired = new ArrayList<>();

        List<String> conflictedSeats = findConflictedSeats(draft.getDraftId(), draft.getPerformanceId(), toAcquire);
        if (!conflictedSeats.isEmpty()) {
            throw new SeatHoldConflictException(conflictedSeats);
        }

        try {
            for (String seatNumber : toAcquire) {
                acquireSeatHold(draft.getDraftId(), draft.getPerformanceId(), seatNumber);
                acquired.add(seatNumber);
            }

            for (String seatNumber : currentSeats) {
                if (targetSet.contains(seatNumber)) {
                    refreshSeatHoldTtl(draft.getDraftId(), draft.getPerformanceId(), seatNumber);
                }
            }

            releaseSeatHolds(draft.getDraftId(), draft.getPerformanceId(), toRelease);
        } catch (RuntimeException ex) {
            releaseSeatHolds(draft.getDraftId(), draft.getPerformanceId(), acquired);
            throw ex;
        }
    }

    private List<String> findConflictedSeats(UUID draftId, Long performanceId, List<String> seatNumbers) {
        List<String> conflictedSeats = new ArrayList<>();

        for (String seatNumber : seatNumbers) {
            Seat seat = seatRepository.findByPerformanceIdAndSeatNumber(performanceId, seatNumber)
                    .orElseThrow(() -> new IllegalArgumentException("좌석이 없습니다: " + seatNumber));

            if (seat.getSeatStatus() == SeatStatus.RESERVED) {
                conflictedSeats.add(seatNumber);
                continue;
            }

            String key = generateSeatHoldKey(performanceId, seatNumber);
            Object owner = redisTemplate.opsForValue().get(key);

            if (owner != null && !Objects.equals(draftId.toString(), owner.toString())) {
                conflictedSeats.add(seatNumber);
            }
        }

        return conflictedSeats;
    }

    private void acquireSeatHold(UUID draftId, Long performanceId, String seatNumber) {
        Seat seat = seatRepository.findByPerformanceIdAndSeatNumber(performanceId, seatNumber)
                .orElseThrow(() -> new IllegalArgumentException("좌석이 없습니다: " + seatNumber));

        if (seat.getSeatStatus() == SeatStatus.RESERVED) {
            throw new IllegalStateException("이미 예매가 완료된 좌석입니다: " + seatNumber);
        }

        String key = generateSeatHoldKey(performanceId, seatNumber);
        String owner = String.valueOf(redisTemplate.opsForValue().get(key));

        if (Objects.equals(owner, draftId.toString())) {
            redisTemplate.expire(key, SEAT_HOLD_TTL);
            return;
        }

        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(key, draftId.toString(), SEAT_HOLD_TTL);
        if (!Boolean.TRUE.equals(acquired)) {
            throw new IllegalStateException("다른 사용자가 이미 선점한 좌석입니다: " + seatNumber);
        }
    }

    private void validateSeatHolds(UUID draftId, Long performanceId, List<String> seatNumbers) {
        for (String seatNumber : seatNumbers) {
            String key = generateSeatHoldKey(performanceId, seatNumber);
            Object owner = redisTemplate.opsForValue().get(key);
            if (!Objects.equals(draftId.toString(), owner == null ? null : owner.toString())) {
                throw new IllegalStateException("좌석 선점이 만료되었거나 다른 사용자가 점유했습니다: " + seatNumber);
            }
        }
    }

    private void refreshSeatHoldTtl(UUID draftId, Long performanceId, String seatNumber) {
        String key = generateSeatHoldKey(performanceId, seatNumber);
        Object owner = redisTemplate.opsForValue().get(key);
        if (Objects.equals(draftId.toString(), owner == null ? null : owner.toString())) {
            redisTemplate.expire(key, SEAT_HOLD_TTL);
        }
    }

    private void releaseSeatHolds(UUID draftId, Long performanceId, List<String> seatNumbers) {
        for (String seatNumber : seatNumbers) {
            String key = generateSeatHoldKey(performanceId, seatNumber);
            Object owner = redisTemplate.opsForValue().get(key);
            if (Objects.equals(draftId.toString(), owner == null ? null : owner.toString())) {
                redisTemplate.delete(key);
            }
        }
    }

    private void writeDraft(ReservationDraftCacheDto draft) {
        redisTemplate.opsForValue().set(generateKey(draft.getDraftId()), draft, DRAFT_TTL);
    }

    private String generateKey(UUID draftId) {
        return KEY_PREFIX + draftId;
    }

    private String generateSeatHoldKey(Long performanceId, String seatNumber) {
        return SEAT_HOLD_KEY_PREFIX + performanceId + ":" + seatNumber;
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
                draft.getStartedAt(),
                draft.getRemainingSeatLimit(),
                draft.getPerformanceUrl(),
                draft.getImageUrl(),
                draft.getSelectedSeats(),
                draft.getTotalPrice(),
                draft.isConfirmed(),
                draft.getReservedDate()
        );
    }

    public void savePaymentInfo(UUID draftId, String paymentKey, Long amount) {
        String key = "reservation:draft:" + draftId;

        ReservationDraftCacheDto draft = (ReservationDraftCacheDto) redisTemplate.opsForValue().get(key);

        if (draft == null) {
            throw new IllegalArgumentException("임시 예매 정보가 없습니다.");
        }

        draft.setPaymentKey(paymentKey);
        draft.setAmount(amount);

        redisTemplate.opsForValue().set(key, draft);
    }

    /**
     * 개요: 대기열 관련 함수들! redis에서 active큐에서 입장 허용된 유저와 유저 정보를 삭제하는 메서드
     * 인자값: 멤버id, 공연id
     * 반환값: 없음
     */
    public void deleteActiveUser(Long memberId, Long performanceId) {
        String activeKey = getActiveKey(performanceId);
        String userQueueKey = getUserQueueKey(performanceId, memberId);
        queueRedisService.removeActiveUser(activeKey, memberId);
        queueRedisService.removeUserInfo(userQueueKey);
    }

    private String getActiveKey(Long performanceId) {
        return "queue:active:" + performanceId;
    }

    private String getUserQueueKey(Long performanceId, Long memberId) {
        return "queue:user:" + performanceId + ":" + memberId;
    }
}
