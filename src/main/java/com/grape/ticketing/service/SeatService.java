package com.grape.ticketing.service;

import com.grape.ticketing.domain.Seat;
import com.grape.ticketing.domain.mapper.SeatMapper;
import com.grape.ticketing.dto.SeatTO;
import com.grape.ticketing.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatService {
    @Autowired
    private final SeatRepository seatRepository;
    @Autowired
    private final SeatMapper seatMapper;

    public List<SeatTO> findAllSeats(Long id) {
        List<Seat> seats = seatRepository.findSeatsByPerformanceId(id);
        List<SeatTO> seatList = seatMapper.toSeatTOList(seats);
        return seatList;

    }
}
