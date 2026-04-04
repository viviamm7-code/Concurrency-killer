package com.grape.ticketing.service;

import com.grape.ticketing.dto.queue.RegisterResponseTO;
import com.grape.ticketing.dto.queue.Status;
import com.grape.ticketing.dto.queue.StatusResponseTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class QueueService {
    private final QueueRedisService queueRedisService;

    /**
     * 개요: 대기열에 예매 요청한 사용자를 등록하는 큐
     * 인자값: memberId, performanceId
     * 반환값: 대기열 등록 정보
     */
    public RegisterResponseTO registerQueue(Long memberId, Long performanceId) {
        String userQueueKey = getUserQueueKey(performanceId, memberId);
        String waitingQueueKey = getWaitingKey(performanceId);

        //등록되어있으면 바로 대기순번 조회 후 응답
        if (queueRedisService.check(userQueueKey)) {
            Status result = queueRedisService.getStatus(userQueueKey);
            Status status = result != null ? result : Status.WAITING;
            Long rank = queueRedisService.getRank(waitingQueueKey, memberId);
            return RegisterResponseTO.builder()
                    .memberId(memberId)
                    .performanceId(performanceId)
                    .status(status)
                    .rank(rank != null ? rank + 1 : null) // 사용자에게는 1부터 보여주기
                    .build();
        }

        //사용자 정보 저장
        Long now = System.currentTimeMillis();
        Map<String, Object> queueInfo = Map.of(
                "userId", memberId.toString(),
                "performanceId", performanceId.toString(),
                "status", Status.WAITING,
                "enteredAt", String.valueOf(now),
                "activeUntil", 0  //수정
        );
        queueRedisService.addUserQueue(userQueueKey, queueInfo);

        //대기열에 저장
        queueRedisService.addWaitingQueue(waitingQueueKey, memberId, now);
        queueRedisService.addPerformance(performanceId);  //대기열 생긴 공연도 저장

        //사용자의 대기순번 조회
        Long rank = queueRedisService.getRank(waitingQueueKey, memberId);

        return RegisterResponseTO.builder()
                .memberId(memberId)
                .performanceId(performanceId)
                .status(Status.WAITING)
                .rank(rank)
                .build();
    }

    /**
     * 개요: 사용자가 현재 입장 가능한 상태인지 조회
     * 인자값: memberId, performanceId
     * 반환값: 사용자의 상태 정보 dto
     */
    public StatusResponseTO getStatus(Long memberId, Long performanceId) {
        String userQueueKey = getUserQueueKey(performanceId, memberId);
        String waitingQueueKey = getWaitingKey(performanceId);

        //userQueueKey로 조회
        Map<Object, Object> userInfo = queueRedisService.getUserInfo(userQueueKey);
        if (userInfo == null || userInfo.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "대기열 정보를 찾을 수 없습니다. 대기열 등록 정보를 다시 해주세요.");
        }  //이거 예외처리 클래스 만들기

        //WAITING 상태면 rank 조회
        Long rank = queueRedisService.getRank(waitingQueueKey, memberId);
        Object value = userInfo.get("activeUntil");
        Long activeUntil = value == null ? null : Long.parseLong(value.toString());

        return StatusResponseTO.builder()
                .memberId(memberId)
                .performanceId(performanceId)
                .status(Status.valueOf(userInfo.get("status").toString()))
                .rank(rank)
                .activeUntil(activeUntil)
                .build();
    }

    private String getUserQueueKey(Long performanceId, Long memberId) {
        return "queue:user:" + performanceId + ":" + memberId;
    }

    private String getWaitingKey(Long performanceId) {
        return "queue:waiting:" + performanceId;
    }
}
