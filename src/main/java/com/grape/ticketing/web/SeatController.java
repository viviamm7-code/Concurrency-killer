package com.grape.ticketing.web;

import com.grape.ticketing.dto.SeatTO;
import com.grape.ticketing.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/performances")
public class SeatController {
    @Autowired
    private SeatService seatService;

    @GetMapping("/{id}/seats")
    public List<SeatTO> getUserById(@PathVariable Long id) {
        return seatService.findAllSeats(id);
    }
}
