package com.grape.ticketing.repository;

import com.grape.ticketing.domain.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat,Long> {

    @Query(value = "SELECT * from seat s where s.performance_id = ?1", nativeQuery = true)
    List<Seat> findAllSeatsByPerformanceId(Long id);
}
