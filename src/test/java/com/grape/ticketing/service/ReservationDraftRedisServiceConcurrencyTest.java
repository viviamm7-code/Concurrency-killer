package com.grape.ticketing.service;

import com.grape.ticketing.domain.Performance;
import com.grape.ticketing.domain.Reservation;
import com.grape.ticketing.domain.ReservationSeat;
import com.grape.ticketing.domain.Seat;
import com.grape.ticketing.domain.member.Member;
import com.grape.ticketing.domain.status.SeatStatus;
import com.grape.ticketing.dto.reservation.ReservationConfirmResponse;
import com.grape.ticketing.dto.reservation.ReservationDraftCreateRequest;
import com.grape.ticketing.dto.reservation.ReservationDraftResponse;
import com.grape.ticketing.dto.reservation.ReservationDraftUpdateRequest;
import com.grape.ticketing.exception.SeatHoldConflictException;
import com.grape.ticketing.repository.MemberRepository;
import com.grape.ticketing.repository.PerformanceRepository;
import com.grape.ticketing.repository.ReservationRepository;
import com.grape.ticketing.repository.ReservationSeatRepository;
import com.grape.ticketing.repository.SeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationDraftRedisServiceConcurrencyTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PerformanceRepository performanceRepository;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private ReservationSeatRepository reservationSeatRepository;
    @Mock
    private SeatRepository seatRepository;

    private ReservationDraftRedisService reservationDraftRedisService;
    private Map<String, Object> redisStore;
    private Seat seatA1;
    private Performance performance;

    @BeforeEach
    void setUp() {
        redisStore = new ConcurrentHashMap<>();
        ValueOperations<String, Object> valueOperations = mock(ValueOperations.class);
        //redis get(key) mocking
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        lenient().when(valueOperations.get(anyString())).thenAnswer(invocation ->
                redisStore.get(invocation.getArgument(0, String.class)));
        //redis set(key value) mocking
        lenient().doAnswer(invocation -> {
            redisStore.put(invocation.getArgument(0, String.class), invocation.getArgument(1));
            return null;
        }).when(valueOperations).set(anyString(), any(), any(Duration.class));
        //redis setIfAbsent(key) mocking
        lenient().when(valueOperations.setIfAbsent(anyString(), any(), any(Duration.class))).thenAnswer(invocation -> {
            String key = invocation.getArgument(0, String.class);
            Object value = invocation.getArgument(1);
            return redisStore.putIfAbsent(key, value) == null;
        });
        lenient().when(redisTemplate.expire(anyString(), any(Duration.class))).thenReturn(true);
        lenient().when(redisTemplate.delete(anyString())).thenAnswer(invocation ->
                redisStore.remove(invocation.getArgument(0, String.class)) != null);

        performance = new Performance();
        performance.setId(1L);
        performance.setPerformanceName("동시성 테스트 공연");
        performance.setPrice(100_000);
        performance.setVenue("잠실주경기장");
        performance.setStartedAt(LocalDateTime.now().plusDays(3));

        seatA1 = new Seat();
        seatA1.setId(101L);
        seatA1.setSeatNumber("A1");
        seatA1.setSeatStatus(SeatStatus.AVAILABLE);
        seatA1.setPerformance(performance);

        //1번공연 반환
        lenient().when(performanceRepository.findById(1L)).thenReturn(Optional.of(performance));
        //A1좌석 반환
        lenient().when(seatRepository.findByPerformanceIdAndSeatNumber(1L, "A1")).thenAnswer(invocation ->
                Optional.of(seatA1));

        AtomicLong reservationIdSequence = new AtomicLong(1L);
        lenient().when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation reservation = invocation.getArgument(0, Reservation.class);
            reservation.setId(reservationIdSequence.getAndIncrement());
            return reservation;
        });
        lenient().when(reservationSeatRepository.save(any(ReservationSeat.class))).thenAnswer(invocation ->
                invocation.getArgument(0, ReservationSeat.class));

        reservationDraftRedisService = new ReservationDraftRedisService(
                redisTemplate,
                memberRepository,
                performanceRepository,
                reservationRepository,
                reservationSeatRepository,
                seatRepository
        );
    }

    @Test
    @DisplayName("1명이 1개 좌석을 선택하고 최종 예매하면 정상적으로 1건의 예매가 완료된다")
    void singleMemberCanReserveOneSeatSuccessfully() {
        long memberId = 1L;
        stubMember(memberId);

        ReservationDraftResponse draft = reservationDraftRedisService.createDraft(createRequest(memberId));
        ReservationDraftResponse updatedDraft = reservationDraftRedisService.updateDraft(
                draft.getDraftId(),
                updateRequest("A1")
        );
        ReservationConfirmResponse confirmResponse = reservationDraftRedisService.confirmDraft(draft.getDraftId());

        assertEquals(List.of("A1"), updatedDraft.getSelectedSeats());
        assertNotNull(confirmResponse.getReservationId());
        assertEquals("최종 예매가 완료되었습니다.", confirmResponse.getMessage());
        assertEquals(SeatStatus.RESERVED, seatA1.getSeatStatus());
        assertTrue(redisStore.keySet().stream().noneMatch(key -> key.contains("seat:hold:1:A1")));
    }

    @Test
    @DisplayName("100명이 동시에 같은 좌석을 예매하면 오직 1명만 좌석 선점과 최종 예매에 성공한다")
    void oneHundredMembersCompeteForSameSeatOnlyOneSucceeds() throws Exception {
        int userCount = 100;
        for (long memberId = 1; memberId <= userCount; memberId++) {
            stubMember(memberId);
        }

        ExecutorService executorService = Executors.newFixedThreadPool(userCount);
        CountDownLatch ready = new CountDownLatch(userCount);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<AttemptResult>> futures = new ArrayList<>();

        for (long memberId = 1; memberId <= userCount; memberId++) {
            final long currentMemberId = memberId;
            futures.add(executorService.submit(() -> {
                ReservationDraftResponse draft = reservationDraftRedisService.createDraft(createRequest(currentMemberId));
                ready.countDown();
                start.await(10, TimeUnit.SECONDS);
                try {
                    reservationDraftRedisService.updateDraft(draft.getDraftId(), updateRequest("A1"));
                    return AttemptResult.success(draft.getDraftId(), currentMemberId);
                } catch (SeatHoldConflictException | IllegalStateException exception) {
                    return AttemptResult.failure(currentMemberId, exception);
                }
            }));
        }

        // 100개의 스레드 준비됐는지 확인하고 한번에 출발
        assertTrue(ready.await(10, TimeUnit.SECONDS));
        start.countDown();

        //결과 집계
        List<AttemptResult> results = new ArrayList<>();
        for (Future<AttemptResult> future : futures) {
            results.add(future.get(10, TimeUnit.SECONDS));
        }
        executorService.shutdownNow();

        List<AttemptResult> seatHoldSuccesses = results.stream().filter(AttemptResult::success).toList();
        List<AttemptResult> failures = results.stream().filter(result -> !result.success()).toList();

        assertEquals(1, seatHoldSuccesses.size(), "동일 좌석은 한 명만 선점해야 합니다.");
        assertEquals(userCount - 1, failures.size(), "나머지 요청은 모두 실패해야 합니다.");

        AttemptResult winner = seatHoldSuccesses.get(0);
        ReservationConfirmResponse confirmResponse = reservationDraftRedisService.confirmDraft(winner.draftId());

        assertNotNull(confirmResponse.getReservationId());
        assertEquals(SeatStatus.RESERVED, seatA1.getSeatStatus());
        assertTrue(
                failures.stream().allMatch(result ->
                        result.exception() instanceof SeatHoldConflictException
                                || result.exception() instanceof IllegalStateException
                ),
                "실패 케이스는 좌석 선점 충돌 또는 이미 점유된 좌석 예외여야 합니다."
        );
    }

    private void stubMember(long memberId) {
        Member member = new Member("user" + memberId, "user" + memberId + "@example.com", "user" + memberId, "1234", "USER");
        member.setId(memberId);
        lenient().when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
    }

    private ReservationDraftCreateRequest createRequest(long memberId) {
        ReservationDraftCreateRequest request = new ReservationDraftCreateRequest();
        request.setMemberId(memberId);
        request.setPerformanceId(1L);
        request.setPerformanceDate("2026-04-10");
        request.setPerformanceTitle("동시성 테스트 공연");
        request.setPerformancePrice(100_000);
        request.setPerformanceVenue("잠실주경기장");
        request.setStartedAt("19:00");
        request.setRemainingSeatLimit(4);
        request.setPerformanceUrl("/performance/1");
        request.setImageUrl("/images/test.png");
        request.setReservedDate(new Date());
        return request;
    }

    private ReservationDraftUpdateRequest updateRequest(String... seatNumbers) {
        ReservationDraftUpdateRequest request = new ReservationDraftUpdateRequest();
        request.setSelectedSeats(List.of(seatNumbers));
        request.setTotalPrice(100_000 * seatNumbers.length);
        return request;
    }

    private record AttemptResult(boolean success, UUID draftId, long memberId, RuntimeException exception) {
        static AttemptResult success(UUID draftId, long memberId) {
            return new AttemptResult(true, draftId, memberId, null);
        }

        static AttemptResult failure(long memberId, RuntimeException exception) {
            return new AttemptResult(false, null, memberId, exception);
        }
    }
}
