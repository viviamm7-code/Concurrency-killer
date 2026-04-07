package com.grape.ticketing.dto.queue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponseTO {

    private Long memberId;
    private Long performanceId;
    private Status status;  //상태값
    private Long rank;  //대기순번
}
