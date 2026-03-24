package com.grape.ticketing.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ReservationDraftCacheDto implements Serializable {
    private UUID draftId;
    private Long memberId;
    private Long performanceId;
    private String performanceDate;
    private String performanceTitle;
    private Integer performancePrice;
    private String performanceVenue;
    private Integer remainingSeatLimit;
    private String performanceUrl;
    private List<String> selectedSeats = new ArrayList<>();
    private Integer totalPrice;
    private boolean confirmed;
}
