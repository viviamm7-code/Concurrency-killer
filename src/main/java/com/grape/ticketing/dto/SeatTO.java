package com.grape.ticketing.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SeatTO {
    private String seatNumber;
    private String seatStatus;
}
