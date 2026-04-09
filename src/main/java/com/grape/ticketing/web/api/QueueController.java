package com.grape.ticketing.web.api;

import com.grape.ticketing.domain.member.Member;
import com.grape.ticketing.dto.queue.InactiveResponseTO;
import com.grape.ticketing.dto.queue.RegisterResponseTO;
import com.grape.ticketing.dto.queue.StatusResponseTO;
import com.grape.ticketing.service.QueueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@RestController
@RequestMapping("api/queue")
@RequiredArgsConstructor
@Tag(name = "대기열 큐 API")
public class QueueController {

    private final QueueService queueService;

    /**
     * 대기열 등록 api
     * 공연 상세 조회에서 예매하기 클릭 시 호출
     */
    @Operation(summary = "대기열 등록")
    @PostMapping("/register")
    public RegisterResponseTO registerWaitingQueue(/*HttpSession session,*/@RequestParam Long memberId, @RequestParam Long performanceId) {
        /*Long memberId = (Long) session.getAttribute("loginMember");
        if (memberId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }*/
        return queueService.registerQueue(memberId, performanceId);
    }

    /**
     * 상태 조회 api
     * 클라이언트가 현재 자신이 입장 가능한 상태인지 조회하는 api
     */
    @Operation(summary = "대기열 상태 조회")
    @GetMapping("/status")
    public StatusResponseTO getStatus(/*HttpSession session,*/@RequestParam Long memberId, @RequestParam Long performanceId) {
         /*Long memberId = (Long) session.getAttribute("loginMember");
        if (memberId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }*/
        return queueService.getStatus(memberId, performanceId);
    }

    /**
     * SSE로 대기인원 조회하는 api
     * 성률 : public SseEmitter stream(HttpSession session, @RequestParam UUID draftId)
     * 이 부분의 세션 사용은 로그인 시스템을 시큐리티로 전환하였으므로 @AuthenticationPrincipal Member member로 수정하였습니다
     */
    @Operation(summary = "SSE로 대기인원 조회")
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@AuthenticationPrincipal Member member, @RequestParam UUID draftId) {
        if (member == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        return queueService.getWaitingInfo(member.getId(), draftId);
    }

    /**
     * active 유저와 사용자 정보 삭제하는 api(예매 완료 시 호출)
     */
    @Operation(summary = "active 유저와 사용자 정보 삭제(예매 완료 시 호출)")
    @DeleteMapping("/inactive")
    public InactiveResponseTO deleteActiveUser(@AuthenticationPrincipal Member member, @RequestParam Long performanceId) {
        if (member == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        queueService.deleteActiveUser(member.getId(), performanceId);
        return InactiveResponseTO.builder()
                .code(200)
                .message("active 유저 삭제 성공")
                .build();
    }
}
