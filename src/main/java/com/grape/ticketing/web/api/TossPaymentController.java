package com.grape.ticketing.web.api;

import com.grape.ticketing.service.TossPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payments/toss")
public class TossPaymentController {

    private final TossPaymentService tossPaymentService;

    @GetMapping("/success")
    public String success(
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam Long amount,
            @RequestParam UUID draftId,
            Model model
    ) {
        tossPaymentService.handleSuccess(draftId, paymentKey, orderId, amount);
        System.out.println("handleSuccess 완료");
        model.addAttribute("message", "결제가 완료되었습니다.");
        model.addAttribute("reservationUrl", "/reservation");
        return "payments/success";
    }

    @GetMapping("/fail")
    public String fail(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String message,
            @RequestParam(required = false) String orderId
    ) {
        System.out.println("결제 실패 code = " + code);
        System.out.println("결제 실패 message = " + message);
        System.out.println("결제 실패 orderId = " + orderId);

        if (orderId != null && !orderId.isBlank()) {
            return "redirect:/reservationConfirm2?draftId=" + orderId;
        }

        return "redirect:/performance-list";
    }
}