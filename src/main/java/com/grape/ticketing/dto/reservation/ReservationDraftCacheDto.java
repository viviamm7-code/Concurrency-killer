package com.grape.ticketing.dto.reservation;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
    private String StartedAt;
    private Integer remainingSeatLimit;
    private String performanceUrl;
    private String imageUrl;
    private List<String> selectedSeats = new ArrayList<>();
    private Integer totalPrice;
    private boolean confirmed;
    private Date reservedDate;

    //결제
    private String paymentKey;
    private Long amount;
}
