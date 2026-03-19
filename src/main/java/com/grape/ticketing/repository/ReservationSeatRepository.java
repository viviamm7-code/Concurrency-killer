package com.grape.ticketing.repository;

import com.grape.ticketing.domain.ReservationSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, Long> {
    @Query("""
    select count(rs)
    from ReservationSeat rs
    join rs.reservation r
    where r.member.id = :memberId
      and r.performance.id = :performanceId
    """)
    long countReservedSeatsByMemberIdAndPerformanceId(@Param("memberId") Long memberId, @Param("performanceId") Long performanceId);
}
