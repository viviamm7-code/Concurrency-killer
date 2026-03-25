package com.grape.ticketing.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ReservationDraftResponse {
    private UUID draftId;
    private Long memberId;
    private Long performanceId;
    private String performanceDate;
    private String performanceTitle;
    private Integer performancePrice;
    private String performanceVenue;
    private Integer remainingSeatLimit;
    private String performanceUrl;
    private String imageUrl;
    private List<String> selectedSeats;
    private Integer totalPrice;
    private boolean confirmed;
}
