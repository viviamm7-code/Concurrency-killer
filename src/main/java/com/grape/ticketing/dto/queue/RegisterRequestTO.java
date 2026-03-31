package com.grape.ticketing.dto.queue;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegisterRequestTO {

    @NotNull(message = "공연 ID는 필수입니다.")
    private Long performanceId;
}
