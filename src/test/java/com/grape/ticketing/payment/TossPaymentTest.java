package com.grape.ticketing.payment;

import com.grape.ticketing.dto.reservation.ReservationConfirmResponse;
import com.grape.ticketing.dto.reservation.ReservationDraftCacheDto;
import com.grape.ticketing.service.PaymentService;
import com.grape.ticketing.service.ReservationDraftRedisService;
import com.grape.ticketing.service.TossPaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TossPaymentTest {

    @Mock
    private ReservationDraftRedisService reservationDraftService;

    @InjectMocks
    private TossPaymentService tossPaymentService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tossPaymentService, "secretKey", "test_sk_dummy");
    }

    @Test
    @DisplayName("orderId와 draftId가 다르면 예외가 발생한다")
    void handleSuccess_fail_whenOrderIdDoesNotMatchDraftId() {
        UUID draftId = UUID.randomUUID();
        String paymentKey = "test_payment_key";
        String orderId = UUID.randomUUID().toString(); // 일부러 다르게
        Long amount = 10000L;

        ReservationDraftCacheDto draft = new ReservationDraftCacheDto();
        draft.setDraftId(draftId);
        draft.setTotalPrice(10000);

        when(reservationDraftService.getDraftEntity(draftId)).thenReturn(draft);

        try {
            tossPaymentService.handleSuccess(draftId, paymentKey, orderId, amount);
            fail("예외가 발생해야 합니다.");
        } catch (IllegalArgumentException e) {
            assertEquals("orderId가 draftId와 일치하지 않습니다.", e.getMessage());
        }
    }

    @Test
    @DisplayName("결제 금액이 다르면 예외가 발생한다")
    void handleSuccess_fail_whenAmountDoesNotMatch() {
        UUID draftId = UUID.randomUUID();
        String paymentKey = "test_payment_key";
        String orderId = draftId.toString();
        Long amount = 20000L; // draft 금액과 다르게

        ReservationDraftCacheDto draft = new ReservationDraftCacheDto();
        draft.setDraftId(draftId);
        draft.setTotalPrice(10000);

        when(reservationDraftService.getDraftEntity(draftId)).thenReturn(draft);

        try {
            tossPaymentService.handleSuccess(draftId, paymentKey, orderId, amount);
            fail("예외가 발생해야 합니다.");
        } catch (IllegalArgumentException e) {
            assertEquals("결제 금액이 일치하지 않습니다.", e.getMessage());
        }
    }

    @Test
    @DisplayName("draft가 없으면 예외가 발생한다")
    void handleSuccess_fail_whenDraftIsNull() {
        UUID draftId = UUID.randomUUID();
        String paymentKey = "test_payment_key";
        String orderId = draftId.toString();
        Long amount = 10000L;

        when(reservationDraftService.getDraftEntity(draftId)).thenReturn(null);

        try {
            tossPaymentService.handleSuccess(draftId, paymentKey, orderId, amount);
            fail("예외가 발생해야 합니다.");
        } catch (IllegalArgumentException e) {
            assertEquals("임시 예매 정보가 없습니다.", e.getMessage());
        }
    }

    @Test
    @DisplayName("검증 통과 후 getDraftEntity와 savePaymentInfo가 호출된다")
    void handleSuccess_success() {
        UUID draftId = UUID.randomUUID();
        String paymentKey = "test_payment_key";
        String orderId = draftId.toString();
        Long amount = 10000L;

        ReservationDraftCacheDto draft = new ReservationDraftCacheDto();
        draft.setDraftId(draftId);
        draft.setTotalPrice(10000);

        when(reservationDraftService.getDraftEntity(draftId)).thenReturn(draft);

        try {
            tossPaymentService.handleSuccess(draftId, paymentKey, orderId, amount);
        } catch (Exception e) {
            // 뒤쪽 외부 호출은 지금 무시
        }

        verify(reservationDraftService).getDraftEntity(draftId);
        verify(reservationDraftService).savePaymentInfo(draftId, paymentKey, amount);
    }
}