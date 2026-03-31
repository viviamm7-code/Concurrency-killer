package com.grape.ticketing.service;

import com.grape.ticketing.dto.queue.RegisterRequestTO;
import com.grape.ticketing.dto.queue.RegisterResponseTO;
import com.grape.ticketing.dto.queue.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class QueueService {
    private final QueueRedisService queueRedisService;

    /**
     * 대기열에 예매 요청한 사용자를 등록하는 큐
     */
    public RegisterResponseTO registerQueue(Long memberId, RegisterRequestTO request) {
        String userQueueKey = getUserQueueKey(request.getPerformanceId(), memberId);
        String waitingQueueKey = getWaitingKey(request.getPerformanceId());

        //등록되어있으면 바로 대기순번 조회 후 응답
        if (queueRedisService.check(userQueueKey)) {
            Status result = queueRedisService.getStatus(userQueueKey);
            Status status = result != null ? result : Status.WAITING;
            Long rank = queueRedisService.getRank(waitingQueueKey, memberId);
            return RegisterResponseTO.builder()
                    .memberId(memberId)
                    .performanceId(request.getPerformanceId())
                    .status(status)
                    .rank(rank != null ? rank + 1 : null) // 사용자에게는 1부터 보여주기
                    .build();
        }

        //사용자 정보 저장
        Long now = System.currentTimeMillis();
        Map<String, Object> queueInfo = Map.of(
                "userId", memberId.toString(),
                "performanceId", request.getPerformanceId().toString(),
                "status", Status.WAITING,
                "enteredAt", String.valueOf(now),
                "activeUntil", "0"  //수정
        );
        queueRedisService.addUserQueue(userQueueKey, queueInfo);

        //대기열에 저장
        queueRedisService.addWaitingQueue(waitingQueueKey, memberId, now);

        //사용자의 대기순번 조회
        Long rank = queueRedisService.getRank(waitingQueueKey, memberId);

        return RegisterResponseTO.builder()
                .memberId(memberId)
                .performanceId(request.getPerformanceId())
                .status(Status.WAITING)
                .rank(rank != null ? rank + 1 : null)
                .build();
    }

    private String getUserQueueKey(Long performanceId, Long memberId) {
        return "queue:user:" + performanceId + ":" + memberId;
    }

    private String getWaitingKey(Long performanceId) {
        return "queue:waiting:" + performanceId;
    }
}
