package com.grape.ticketing.service;

import com.grape.ticketing.repository.PerformanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerformanceService {
    private final PerformanceRepository performanceRepository;
}
