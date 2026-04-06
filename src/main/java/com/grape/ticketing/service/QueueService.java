package com.grape.ticketing.service;

import com.grape.ticketing.dto.queue.RegisterResponseTO;
import com.grape.ticketing.dto.queue.Status;
import com.grape.ticketing.dto.queue.StatusResponseTO;
import com.grape.ticketing.dto.queue.WaitingInfo;
import com.grape.ticketing.dto.reservation.ReservationDraftResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class QueueService {
    private final QueueRedisService queueRedisService;
    private final ReservationDraftRedisService draftRedisService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);  //sse전송 스레드 수 설정(4개)

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

        System.out.println("대기큐 인원수: " + queueRedisService.getWaitingCount(waitingQueueKey));
        //사용자 정보 저장
        Long now = System.currentTimeMillis();
        Map<String, Object> queueInfo = Map.of(
                "userId", memberId.toString(),
                "performanceId", performanceId.toString(),
                "status", Status.WAITING,
                "enteredAt", String.valueOf(now),
                "activeUntil", 0,  //수정
                "initialRank", queueRedisService.getWaitingCount(waitingQueueKey) + 1
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

    /**
     * 개요: SSE를 통해 클라이언트에게 공연 정보와 대기인원 정보를 보내주는 메서드입니다.
     * 인자값: memberId, draftId
     * 반환값: SSE emitter
     */
    public SseEmitter getWaitingInfo(Long userId, UUID draftId) {
        SseEmitter emitter = new SseEmitter(2 * 60 * 60 * 1000L);  //타임아웃 시간 설정(밀리초) - 2시간
        //주기적으로 실행할 작업 지정
        ScheduledFuture<?> task = scheduler.scheduleAtFixedRate(() -> {
            try {
                WaitingInfo info = getRedisWaitingInfo(userId, draftId);
                Map<String, Object> infoMap = createWaitingInfoMap(info);

                //대기순번 정보 전송
                emitter.send(SseEmitter.event()
                        .name("waiting-info")  //이벤트의 이름 - 클라이언트에서도 동일하게 지정해야함
                        .data(infoMap));

                //입장 허용 상태(ACTIVE)면 연결 종료
                if ("ACTIVE".equals(info.getStatus().toString())) {
                    emitter.complete();         //정상 종료. onCompletion이 호출되어 task 정리됨
                }

            } catch (Exception e) {
                emitter.completeWithError(e);  //에러 발생 시 종료. onError에서 task 정리됨
            }
        }, 0, 30, TimeUnit.SECONDS);  //30초마다 전송

        // task 정리
        Runnable cleanup = () -> task.cancel(true);  //task 끝내는 작업
        emitter.onCompletion(cleanup);  //정상 종료 task 종료
        emitter.onTimeout(cleanup);  //타임아웃 시 task 종료
        emitter.onError(e -> cleanup.run());  //에러 발생 시에도 task 종료
        return emitter;
    }

    //redis에서 공연, 대기인원(순번) 정보 조회
    private WaitingInfo getRedisWaitingInfo(Long userId, UUID draftId) {
        int waitMin = 0;
        //draft에서 공연 정보 조회
        ReservationDraftResponse draft = draftRedisService.getDraft(draftId);
        if (draft == null) {
            throw new RuntimeException();
        }

        String userQueueKey = getUserQueueKey(draft.getPerformanceId(), draft.getMemberId());
        String waitingQueueKey = getWaitingKey(draft.getPerformanceId());

        Long rank = queueRedisService.getRank(waitingQueueKey, userId);
        Status status = queueRedisService.getStatus(userQueueKey);
        Map<Object, Object> userInfo = queueRedisService.getUserInfo(userQueueKey);
        int initialRank = (int) userInfo.get("initialRank");
        double percent = 0;
        if (initialRank != 0 && rank != null) {
            percent = (double) (initialRank - rank) / initialRank * 100;
        }

        return WaitingInfo.builder()
                .title(draft.getPerformanceTitle())
                .venue(draft.getPerformanceVenue())
                .date(draft.getPerformanceDate())
                .price(draft.getPerformancePrice().toString())
                .status(status)
                .rank(rank)
                .waitMin(waitMin)
                .percent(percent)
                .build();
    }

    //redis에서 가져온 데이터를 Map으로 변환
    private Map<String, Object> createWaitingInfoMap(WaitingInfo info) {
        Map<String, Object> data = new HashMap<>();
        data.put("status",    info.getStatus());       // "WAITING" or "ACTIVE"
        data.put("title", info.getTitle());
        data.put("venue",     info.getVenue());
        data.put("date",    info.getDate());
        data.put("price",     info.getPrice());
        data.put("rank",     info.getRank());
        data.put("waitMin",   info.getWaitMin());
        data.put("percent",       info.getPercent());
        return data;
    }

    private String getUserQueueKey(Long performanceId, Long memberId) {
        return "queue:user:" + performanceId + ":" + memberId;
    }

    private String getWaitingKey(Long performanceId) {
        return "queue:waiting:" + performanceId;
    }
}
