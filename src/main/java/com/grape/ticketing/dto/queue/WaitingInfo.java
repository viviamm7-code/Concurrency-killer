package com.grape.ticketing.dto.queue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaitingInfo {

    private String title;
    private String venue;
    private String date;
    private String price;
    private Status status;  //WAITING or ACTIVE
    private long rank;
    private long waitMin;  //예상 대기 시간
    private double percent;  //진행률
}
