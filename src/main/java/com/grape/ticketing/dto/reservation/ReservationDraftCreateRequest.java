package com.grape.ticketing.dto.reservation;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ReservationDraftCreateRequest {
    private Long memberId;
    private Long performanceId;
    private String performanceDate;
    private String performanceTitle;
    private Integer performancePrice;
    private String performanceVenue;
    private String startedAt;
    private Integer remainingSeatLimit;
    private String performanceUrl;
    private String imageUrl;
    private Date reservedDate;
}
