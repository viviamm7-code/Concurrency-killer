package com.grape.ticketing.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReservationDraftUpdateRequest {
    private String performanceDate;
    private String performanceTitle;
    private Integer performancePrice;
    private String performanceVenue;
    private Integer remainingSeatLimit;
    private String performanceUrl;
    private List<String> selectedSeats;
    private Integer totalPrice;
}
