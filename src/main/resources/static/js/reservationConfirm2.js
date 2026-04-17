const fallbackImage = "https://images.unsplash.com/photo-1503095396549-807759245b35?auto=format&fit=crop&w=1200&q=80";
let currentDraft = null;

const clientKey = window.TOSS_CLIENT_KEY;
console.log("TOSS_CLIENT_KEY =", clientKey);
console.log("typeof TossPayments =", typeof TossPayments);

const tossPayments = TossPayments(clientKey);

// 회원 결제용 customerKey
const customerKey = `member_${Date.now()}`;
const payment = tossPayments.payment({ customerKey });

function getDraftIdFromQuery() {
    const params = new URLSearchParams(window.location.search);
    return params.get("draftId");
}

function formatPrice(price) {
    if (price == null) return "-";
    return Number(price).toLocaleString("ko-KR") + "원";
}

function formatMemberLabel(memberId) {
    if (memberId == null) return "-";
    return `회원 #${memberId}`;
}

function renderDetail(draft) {
    const seats = draft.selectedSeats || [];
    const imageUrl = draft.imageUrl || fallbackImage;
    const totalPrice = draft.totalPrice ?? draft.performancePrice ?? 0;

    return `
        <div class="detail-card">
            <img class="poster" src="${imageUrl}" alt="${draft.performanceTitle || '공연 포스터'}">

            <div class="content-box">
                <div class="title">${draft.performanceTitle || "-"}</div>

                <div class="info-grid">
                    <div class="info-item">
                        <div class="info-label">임시 예매번호</div>
                        <div class="info-value">${draft.draftId ?? "-"}</div>
                    </div>

                    <div class="info-item">
                        <div class="info-label">예매자 아이디</div>
                        <div class="info-value">${formatMemberLabel(draft.memberId)}</div>
                    </div>

                    <div class="info-item">
                        <div class="info-label">공연장</div>
                        <div class="info-value">${draft.performanceVenue || "-"}</div>
                    </div>

                    <div class="info-item">
                        <div class="info-label">총 결제금액</div>
                        <div class="info-value">${formatPrice(totalPrice)}</div>
                    </div>

                    <div class="info-item">
                        <div class="info-label">공연일시</div>
                        <div class="info-value">${draft.performanceDate || "-"}</div>
                    </div>

                    <div class="info-item">
                        <div class="info-label">예매 상태</div>
                        <div class="info-value">${draft.confirmed ? "예매 완료" : "확정 전"}</div>
                    </div>
                </div>

                <div class="seat-section">
                    <div class="seat-title">선택 좌석</div>
                    <div class="seat-list">
                        ${
        seats.length > 0
            ? seats.map(seat => `<div class="seat-item">${seat}</div>`).join("")
            : `<div class="seat-item">좌석 없음</div>`
    }
                    </div>
                </div>

                <button class="confirm-btn" id="confirmBtn">결제 하기</button>
            </div>
        </div>
    `;
}

async function requestTossPayment() {
    if (!currentDraft?.draftId) {
        alert("임시 예매 정보가 없습니다.");
        return;
    }

    const confirmBtn = document.getElementById("confirmBtn");

    try {
        if (confirmBtn) {
            confirmBtn.disabled = true;
        }

        console.log("currentDraft =", currentDraft);

        await payment.requestPayment({
            method: "CARD",
            amount: {
                currency: "KRW",
                value: Number(currentDraft.totalPrice)
            },
            orderId: currentDraft.draftId,
            orderName: `${currentDraft.performanceTitle} 예매`,
            successUrl: window.location.origin + `/payments/toss/success?draftId=${currentDraft.draftId}`,
            failUrl: window.location.origin + `/payments/toss/fail?draftId=${currentDraft.draftId}`,
            customerName: `회원 ${currentDraft.memberId}`
        });

        alert(`예매가 완료되었습니다.`);
        window.location.href = "/reservation";
    } catch (e) {
        if (confirmBtn) {
            confirmBtn.disabled = false;
        }
        console.error("토스 결제 요청 오류 =", e);
        alert(e.message || "결제 요청 중 오류가 발생했습니다.");
    }
}

async function loadReservationConfirm() {
    const content = document.getElementById("content");
    const draftId = getDraftIdFromQuery();

    if (!draftId) {
        content.innerHTML = `<div class="error-box">draftId가 없습니다.</div>`;
        return;
    }

    try {
        const response = await fetch(`/reservation-drafts/${draftId}`);

        let payload = null;
        try {
            payload = await response.json();
        } catch (jsonError) {
            payload = null;
        }

        if (!response.ok) {
            throw new Error(payload?.message || "예매 확인 조회 실패");
        }

        currentDraft = payload;
        console.log("currentDraft =", currentDraft);

        content.innerHTML = renderDetail(payload);

        const confirmBtn = document.getElementById("confirmBtn");
        if (confirmBtn) {
            confirmBtn.addEventListener("click", requestTossPayment);
        }
    } catch (e) {
        console.error(e);
        content.innerHTML = `<div class="error-box">예매 정보를 불러오지 못했습니다.\n${e.message}</div>`;
    }
}

loadReservationConfirm();