package com.grape.ticketing.dto.queue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusResponseTO {

    private Long memberId;
    private Long performanceId;
    private Status status;
    private Long rank;
    private Long activeUntil;  //ACTIVE 만료 시각
}
