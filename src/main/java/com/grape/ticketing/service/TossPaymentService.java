package com.grape.ticketing.service;

import com.grape.ticketing.domain.Reservation;
import com.grape.ticketing.dto.reservation.ReservationConfirmResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TossPaymentService {

    @Value("${toss.secret-key}")
    private String secretKey;

    private final ReservationDraftRedisService reservationDraftService;
    private final PaymentService paymentService;
    private final RestClient restClient = RestClient.create();

    @Transactional
    public void handleSuccess(UUID draftId, String paymentKey, String orderId, Long amount) {
        var draft = reservationDraftService.getDraftEntity(draftId);

        if (draft == null) {
            throw new IllegalArgumentException("임시 예매 정보가 없습니다.");
        }
        // 1, draft 에 데이터 저장
        reservationDraftService.savePaymentInfo(draftId, paymentKey, amount);

        // 2. draft와 successUrl 값 비교
        UUID orderUuid = UUID.fromString(orderId);

        if (!draft.getDraftId().equals(orderUuid)) {
            throw new IllegalArgumentException("orderId가 draftId와 일치하지 않습니다.");
        }

        if (!Long.valueOf(draft.getTotalPrice()).equals(amount)) {
            throw new IllegalArgumentException("결제 금액이 일치하지 않습니다.");
        }

        // 3. 토스 승인 API 호출
        String encodedAuth = Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

        String responseBody = restClient.post()
                .uri("https://api.tosspayments.com/v1/payments/confirm")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Map.of(
                        "paymentKey", paymentKey,
                        "orderId", orderId,
                        "amount", amount
                ))
                .retrieve()
                .body(String.class);

        System.out.println("토스 승인 응답 = " + responseBody);

        // 4. 승인 성공 후 예매 확정 로직 실행
        ReservationConfirmResponse confirmResponse = reservationDraftService.confirmDraft(draftId);

        // 5. 결제 테이블에 데이터 넣기
        paymentService.savePayment(
                confirmResponse.getReservationId(),
                paymentKey,
                orderId,
                amount
        );

    }
}