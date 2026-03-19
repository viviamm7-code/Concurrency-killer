package com.grape.ticketing.repository;

import com.grape.ticketing.domain.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat,Long> {
    List<Seat> findSeatsByPerformanceId(Long performanceId);
}
