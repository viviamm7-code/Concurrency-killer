package com.grape.ticketing.repository;

import com.grape.ticketing.domain.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance,Long> {
}
