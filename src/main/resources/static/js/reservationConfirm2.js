const fallbackImage = "https://images.unsplash.com/photo-1503095396549-807759245b35?auto=format&fit=crop&w=1200&q=80";
let currentDraft = null;

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
                        <div class="info-label">예매자</div>
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
                        ${seats.length > 0
            ? seats.map(seat => `<div class="seat-item">${seat}</div>`).join("")
            : `<div class="seat-item">좌석 없음</div>`
        }
                    </div>
                </div>

                <button class="confirm-btn" id="confirmBtn">예매 완료</button>
            </div>
        </div>
    `;
}

async function confirmReservation() {
    if (!currentDraft?.draftId) {
        alert("임시 예매 정보가 없습니다.");
        return;
    }

    const confirmBtn = document.getElementById("confirmBtn");

    try {
        if (confirmBtn) {
            confirmBtn.disabled = true;
        }

        const response = await fetch(`/reservation-drafts/${currentDraft.draftId}/confirm`, {
            method: "POST"
        });

        let payload = null;
        try {
            payload = await response.json();
        } catch (jsonError) {
            payload = null;
        }

        if (!response.ok) {
            throw new Error(payload?.message || "예매 완료 처리 실패");
        }

        //예매 완료 후 대기열 active큐에서 삭제
        await deleteActiveUser();

        alert(`예매가 완료되었습니다.`);
        window.location.href = "/reservation";
    } catch (e) {
        if (confirmBtn) {
            confirmBtn.disabled = false;
        }
        alert(e.message || "예매 완료 중 오류가 발생했습니다.");
    }
}

//draft가 없어서 에러남 draft 조회하는 코드에서 변수를 밖으로 빼서 공통으로 써야함
async function deleteActiveUser() {
    const response = await fetch(
        `/api/queue/inactive?memberId=${currentDraft.memberId}&performanceId=${currentDraft.performanceId}`,
        { method: 'DELETE' }
    );

    if (!response.ok) {
        throw new Error('active 유저 삭제 실패');
    }

    return await response.json();
}

async function loadReservationConfirm() {
    const content = document.getElementById("content");
    const draftId = getDraftIdFromQuery();

    if (!draftId) {
        content.innerHTML = `<div class="error-box">draftId가 없습니다.</div>`;
        return;
    }

    const backBtn = document.querySelector(".back-btn");
    if (backBtn) {
        backBtn.href = `/seat?draftId=${draftId}`;

        backBtn.addEventListener("click", async (event) => {
            event.preventDefault();

            try {
                const response = await fetch(`/reservation-drafts/${draftId}/release-seats`, {
                    method: "POST"
                });

                let payload = null;
                try {
                    payload = await response.json();
                } catch (jsonError) {
                    payload = null;
                }

                if (!response.ok) {
                    throw new Error(payload?.message || "좌석 선점 해제 실패");
                }

                window.location.href = `/seat?draftId=${draftId}`;
            } catch (e) {
                alert(e.message || "좌석 변경 중 오류가 발생했습니다.");
            }
        });
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
        content.innerHTML = renderDetail(payload);

        const confirmBtn = document.getElementById("confirmBtn");
        if (confirmBtn) {
            confirmBtn.addEventListener("click", confirmReservation);
        }
    } catch (e) {
        content.innerHTML = `<div class="error-box">예매 정보를 불러오지 못했습니다.\n${e.message}</div>`;
    }
}

loadReservationConfirm();
