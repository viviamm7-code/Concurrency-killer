package com.grape.ticketing.service;

import com.grape.ticketing.dto.queue.RegisterResponseTO;
import com.grape.ticketing.dto.queue.Status;
import com.grape.ticketing.dto.queue.StatusResponseTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QueueRedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final Duration USER_QUEUE_TTL = Duration.ofHours(2);  //2시간 후 만료
    private static final Duration WAITING_QUEUE_TTL = Duration.ofHours(5);  //5시간 후 만료
    private static final Duration ACTIVE_QUEUE_TTL = Duration.ofHours(5);  //5시간 후 만료
    private static final String PERFORMANCE_SET_KEY = "queue:performances";

    //이미 등록됐는지 확인
    public boolean check(String userQueueKey) {
        return redisTemplate.hasKey(userQueueKey);
    }

    //유저큐에 추가
    public void addUserQueue(String userQueueKey, Map<String, Object> queueInfo) {
        redisTemplate.opsForHash().putAll(userQueueKey, queueInfo);
        redisTemplate.expire(userQueueKey, USER_QUEUE_TTL);  //TTL 설정
    }

    //대기큐에 추가
    public void addWaitingQueue(String waitingKey, Long userId, Long now) {
        redisTemplate.opsForZSet().add(waitingKey, userId, now);
        redisTemplate.expire(waitingKey, WAITING_QUEUE_TTL);  //TTL 설정 -> zset에 추가될 때마다 TTL 리셋
    }

    //대기순번 조회
    public Long getRank(String waitingKey, Long userId) {
        Long rank = redisTemplate.opsForZSet().rank(waitingKey, userId);
        return rank != null ? rank + 1 : 0;
    }

    //상태 조회
    public Status getStatus(String userQueueKey) {
        return Status.valueOf(redisTemplate.opsForHash().get(userQueueKey, "status").toString());
    }

    //유저 모든 정보 조회
    public Map<Object, Object> getUserInfo(String userQueueKey) {
        return redisTemplate.opsForHash().entries(userQueueKey);
    }

    //유저 정보 삭제
    public void removeUserInfo(String userQueueKey) {
        redisTemplate.delete(userQueueKey);
    }

    //유저 상태 active로 변경
    public void updateUserStatusToActive(String userQueueKey, long activeUntilMillis) {
        redisTemplate.opsForHash().put(userQueueKey, "status", Status.ACTIVE.name());
        redisTemplate.opsForHash().put(userQueueKey, "activeUntil", String.valueOf(activeUntilMillis));
        redisTemplate.expire(userQueueKey, USER_QUEUE_TTL);
    }

    //대기열 가진 공연 저장
    public void addPerformance(Long performanceId) {
        redisTemplate.opsForSet().add(PERFORMANCE_SET_KEY, performanceId.toString());
        redisTemplate.expire(PERFORMANCE_SET_KEY, WAITING_QUEUE_TTL);  //대기열 TTL과 동일하게 설정(2시간)
    }

    //대기열 가진 공연 조회
    public Set<Long> getRegisteredPerformanceIds() {
        Set<Object> members = redisTemplate.opsForSet().members(PERFORMANCE_SET_KEY);
        if (members == null || members.isEmpty()) {
            return Collections.emptySet();
        }
        return members.stream()
                .map(value -> Long.parseLong(value.toString()))
                .collect(Collectors.toSet());
    }

    //대기열 가진 공연 삭제
    public void removeManagedPerformance(Long performanceId) {
        redisTemplate.opsForSet().remove(PERFORMANCE_SET_KEY, performanceId.toString());
    }

    //대기큐에서 pop
    public Set<Object> popWaitingUsers(String waitingKey, long count) {
        if (count <= 0) {
            return Collections.emptySet();
        }
        Set<Object> users = redisTemplate.opsForZSet().range(waitingKey, 0, count - 1);
        if (users == null || users.isEmpty()) {
            return Collections.emptySet();
        }

        redisTemplate.opsForZSet().remove(waitingKey, users.toArray());
        return users;
    }

    //active큐에 저장
    public void addActiveUser(String activeKey, Long userId) {
        redisTemplate.opsForSet().add(activeKey, userId.toString());
        redisTemplate.expire(activeKey, ACTIVE_QUEUE_TTL);
    }

    //active큐에서 삭제
    public void removeActiveUser(String activeKey, Long userId) {
        redisTemplate.opsForSet().remove(activeKey, userId.toString());
    }

    //active큐 조회
    public Set<Long> getActiveUsers(String activeKey) {
        Set<Object> members = redisTemplate.opsForSet().members(activeKey);
        if (members == null || members.isEmpty()) {
            return Collections.emptySet();
        }

        return members.stream()
                .map(value -> Long.parseLong(value.toString()))
                .collect(Collectors.toSet());
    }

    //active큐 크기 조회
    public Long getActiveCount(String activeKey) {
        Long size = redisTemplate.opsForSet().size(activeKey);
        return size == null ? 0L : size;
    }

    //waiting큐 크기 조회
    public Long getWaitingCount(String waitingQueueKey) {
        Long size = redisTemplate.opsForZSet().size(waitingQueueKey);
        return size == null ? 0L : size;
    }

    //큐 비었는지 검사
    public boolean isWaitingQueueEmpty(String waitingKey) {
        Long size = redisTemplate.opsForZSet().size(waitingKey);  //대기큐 크기 조회
        return (size == null ? 0L : size) == 0L;
    }

    public boolean isActiveQueueEmpty(String activeKey) {
        return getActiveCount(activeKey) == 0L;
    }

    //공연 정보 조회

}
