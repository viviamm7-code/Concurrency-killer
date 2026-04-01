package com.grape.ticketing.web.api;

import com.grape.ticketing.dto.queue.RegisterResponseTO;
import com.grape.ticketing.dto.queue.StatusResponseTO;
import com.grape.ticketing.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/queue")
@RequiredArgsConstructor
public class QueueController {

    private final QueueService queueService;

    /**
     * 대기열 등록 api
     * 공연 상세 조회에서 예매하기 클릭 시 호출
     */
    @PostMapping("/register")
    public RegisterResponseTO registerWaitingQueue(/*HttpSession session,*/ @RequestParam Long performanceId) {
        /*Long memberId = (Long) session.getAttribute("loginMember");
        if (memberId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }*/
        Long memberId = 1L;  //임시
        RegisterResponseTO result = queueService.registerQueue(memberId, performanceId);
        System.out.println(result.getMemberId());
        return result;
    }

    /**
     * 상태 조회 api
     * 클라이언트가 현재 자신이 입장 가능한 상태인지 조회하는 api
     */
    @GetMapping("/status")
    public StatusResponseTO getStatus(/*HttpSession session,*/ @RequestParam Long performanceId) {
         /*Long memberId = (Long) session.getAttribute("loginMember");
        if (memberId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }*/
        Long memberId = 1L;
        return queueService.getStatus(memberId, performanceId);
    }
}
