package com.grape.ticketing.service;

import com.grape.ticketing.domain.ReservationSeat;
import com.grape.ticketing.repository.ReservationRepository;
import com.grape.ticketing.repository.ReservationSeatRepository;
import jakarta.xml.bind.SchemaOutputResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {
    private static final long MAX_SEATS_PER_MEMBER = 4;
    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;


    public long getReservedSeatCount(Long memberId, Long performanceId) {
        long reservedSeatCnt = reservationSeatRepository.countReservedSeatsByMemberIdAndPerformanceId(memberId, performanceId);
        return reservedSeatCnt;
    }

    public long getRemainingSeatCount(Long memberId, Long performanceId) {
        long reservedSeatCount = getReservedSeatCount(memberId, performanceId);
        long remainingSeatCount = MAX_SEATS_PER_MEMBER - reservedSeatCount;
        // 시스템상 오류로 잔여 수가 마이너스일 것을 대비해서 max 함수 사용
        return Math.max(remainingSeatCount, 0);
    }

}
