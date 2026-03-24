package com.grape.ticketing.repository;

import com.grape.ticketing.domain.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface SeatRepository extends JpaRepository<Seat,Long> {
    List<Seat> findSeatsByPerformanceId(Long performanceId);
    List<Seat> findAllByPerformanceIdAndSeatNumberIn(Long performanceId, List<String> seatNumbers);
    Optional<Seat> findByPerformanceIdAndSeatNumber(Long performanceId, String seatNumber);
}
