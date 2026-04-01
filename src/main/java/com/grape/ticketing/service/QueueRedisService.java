package com.grape.ticketing.service;

import com.grape.ticketing.dto.queue.RegisterResponseTO;
import com.grape.ticketing.dto.queue.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QueueRedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final Duration USER_QUEUE_TTL = Duration.ofHours(1);  //1시간 후 만료
    private static final Duration WAITING_QUEUE_TTL = Duration.ofHours(5);  //5시간 후 만료

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
        return redisTemplate.opsForZSet().rank(waitingKey, userId);
    }

    //상태 조회
    public Status getStatus(String userQueueKey) {
        return Status.valueOf(redisTemplate.opsForHash().get(userQueueKey, "status").toString());
    }
}
