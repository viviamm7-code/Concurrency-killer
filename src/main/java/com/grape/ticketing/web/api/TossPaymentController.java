package com.grape.ticketing.web.api;

import com.grape.ticketing.service.TossPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "TossPayment 결제 API")
public class TossPaymentController {

    private final TossPaymentService tossPaymentService;

    @Operation(summary = "결제 성공 Redirection")
    @GetMapping("/success")
    public String success(
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam Long amount,
            @RequestParam UUID draftId,
            Model model
    ) {
        tossPaymentService.handleSuccess(draftId, paymentKey, orderId, amount);
        model.addAttribute("message", "결제가 완료되었습니다.");
        model.addAttribute("reservationUrl", "/reservation");
        return "payments/success";
    }

    @Operation(summary = "결제 실패 Redirection")
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