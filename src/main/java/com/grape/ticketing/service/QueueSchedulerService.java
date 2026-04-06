package com.grape.ticketing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class QueueSchedulerService {

    private final QueueRedisService queueRedisService;
    private static final long MAX_ACTIVE = 2L;  //입장 가능한 최대 인원
    private static final long ACTIVE_DURATION_MILLIS = 60 * 1000L;  //입장 허용 시간(1분)

    /**
     * 개요: 스케줄러를 통해 유저의 대기 상태를 2초마다 업데이트하는 메서드
     */
    @Scheduled(fixedDelay = 5000)
    public void updateStatus() {
        //공연 조회
        Set<Long> performanceIds = queueRedisService.getRegisteredPerformanceIds();
        if (performanceIds.isEmpty()) {
            return;
        }

        for (Long performanceId : performanceIds) {
            try {
                processPerformanceQueue(performanceId);
            } catch (Exception e) {
                log.error("대기열 스케줄러 처리 중 오류. performanceId={}", performanceId, e);
            }
        }
    }

    //각 공연에 대해 상태 처리
    private void processPerformanceQueue(Long performanceId) {
        String activeKey = getActiveKey(performanceId);
        String waitingKey = getWaitingKey(performanceId);

        // 1. 만료된 ACTIVE 유저 제거
        expireActiveUsers(performanceId, activeKey);

        // 2. 현재 ACTIVE 인원 수 확인
        long activeCount = queueRedisService.getActiveCount(activeKey);
        long availableSlots = MAX_ACTIVE - activeCount;

        if (availableSlots > 0) {
            // 3. WAITING -> ACTIVE 승급
            promoteWaitingUsers(performanceId, waitingKey, activeKey, availableSlots);
        }

        // 4. waiting / active 모두 비었으면 관리 목록에서 제거
        boolean waitingEmpty = queueRedisService.isWaitingQueueEmpty(waitingKey);
        boolean activeEmpty = queueRedisService.isActiveQueueEmpty(activeKey);
        if (waitingEmpty && activeEmpty) {
            queueRedisService.removeManagedPerformance(performanceId);
        }
    }

    //만료된 active 유저 제거
    private void expireActiveUsers(Long performanceId, String activeKey) {
        long now = System.currentTimeMillis();
        Set<Long> activeUsers = queueRedisService.getActiveUsers(activeKey);

        for (Long memberId : activeUsers) {
            String userQueueKey = getUserQueueKey(performanceId, memberId);
            Map<Object, Object> userInfo = queueRedisService.getUserInfo(userQueueKey);

            if (userInfo == null || userInfo.isEmpty()) {
                queueRedisService.removeActiveUser(activeKey, memberId);
                continue;
            }

            Object activeUntilObj = userInfo.get("activeUntil");
            if (activeUntilObj == null) {
                queueRedisService.removeActiveUser(activeKey, memberId);
                queueRedisService.removeUserInfo(userQueueKey);
                continue;
            }

            long activeUntil = Long.parseLong(activeUntilObj.toString());
            if (activeUntil <= now) {  //만료시간이 지났으면 삭제
                queueRedisService.removeActiveUser(activeKey, memberId);
                queueRedisService.removeUserInfo(userQueueKey);
            }
        }
    }

    //WAITING유저 활성화
    private void promoteWaitingUsers(Long performanceId, String waitingKey, String activeKey, long availableSlots) {
        Set<Object> waitingUsers = queueRedisService.popWaitingUsers(waitingKey, availableSlots);  //남는 자리만큼 대기큐에서 pop
        if (waitingUsers.isEmpty()) {
            return;
        }

        long now = System.currentTimeMillis();
        long activeUntil = now + ACTIVE_DURATION_MILLIS;

        for (Object userObj : waitingUsers) {
            Long memberId = Long.parseLong(userObj.toString());
            String userQueueKey = getUserQueueKey(performanceId, memberId);

            queueRedisService.updateUserStatusToActive(userQueueKey, activeUntil);  //유저 active상태로 변경
            queueRedisService.addActiveUser(activeKey, memberId);  //active큐에도 추가
        }
    }

    private String getUserQueueKey(Long performanceId, Long memberId) {
        return "queue:user:" + performanceId + ":" + memberId;
    }

    private String getWaitingKey(Long performanceId) {
        return "queue:waiting:" + performanceId;
    }

    private String getActiveKey(Long performanceId) {
        return "queue:active:" + performanceId;
    }
}
