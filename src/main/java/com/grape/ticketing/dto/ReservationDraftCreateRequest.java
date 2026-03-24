package com.grape.ticketing.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationDraftCreateRequest {
    private Long memberId;
    private Long performanceId;
    private String performanceDate;
    private String performanceTitle;
    private Integer performancePrice;
    private String performanceVenue;
    private Integer remainingSeatLimit;
    private String performanceUrl;
}
