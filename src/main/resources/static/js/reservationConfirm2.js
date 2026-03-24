const fallbackImage = "https://images.unsplash.com/photo-1503095396549-807759245b35?auto=format&fit=crop&w=1200&q=80";
let currentReservation = null;

function getReservationIdFromPath() {
    const pathParts = window.location.pathname.split("/");
    return pathParts[pathParts.length - 2];
}

function formatDateTime(value) {
    if (!value) return "-";

    const date = new Date(value);
    if (isNaN(date)) return value;

    return date.toLocaleString("ko-KR", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit"
    });
}

function formatPrice(price) {
    if (price == null) return "-";
    return Number(price).toLocaleString("ko-KR") + "원";
}

function renderDetail(data) {
    const seats = data.seatNumbers || [];
    const imageUrl = data.imageUrl || fallbackImage;

    return `
            <div class="detail-card">
                <img class="poster" src="${imageUrl}" alt="${data.performanceName || '공연 포스터'}">

                <div class="content-box">
                    <div class="title">${data.performanceName || "-"}</div>

                    <div class="info-grid">
                        <div class="info-item">
                            <div class="info-label">예매번호</div>
                            <div class="info-value">${data.reservationId ?? "-"}</div>
                        </div>

                        <div class="info-item">
                            <div class="info-label">예매자</div>
                            <div class="info-value">${data.reservationName || "-"}</div>
                        </div>

                        <div class="info-item">
                            <div class="info-label">공연장</div>
                            <div class="info-value">${data.venue || "-"}</div>
                        </div>

                        <div class="info-item">
                            <div class="info-label">가격</div>
                            <div class="info-value">${formatPrice(data.price)}</div>
                        </div>

                        <div class="info-item">
                            <div class="info-label">공연일시</div>
                            <div class="info-value">${formatDateTime(data.startedAt)}</div>
                        </div>

                        <div class="info-item">
                            <div class="info-label">예매일시</div>
                            <div class="info-value">${formatDateTime(data.reservedAt)}</div>
                        </div>
                    </div>

                    <div class="seat-section">
                        <div class="seat-title">예매 좌석</div>
                        <div class="seat-list">
                            ${
        seats.length > 0
            ? seats.map(seat => `<div class="seat-item">${seat}</div>`).join("")
            : `<div class="seat-item">좌석 없음</div>`
    }
                        </div>
                    </div>

                    <button class="confirm-btn" onclick="confirmReservation()">예매 완료</button>
                </div>
            </div>
        `;
}

async function confirmReservation() {
    try {
        console.log("currentReservation =", currentReservation);
        const payload = {
            memberId: currentReservation.memberId,
            performanceId: currentReservation.performanceId,
            seatNumbers: currentReservation.seatNumbers
        };

        console.log("payload = ", payload);

        const response = await fetch("/api/reservation/confirm", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const text = await response.text();
            throw new Error(text || "예매 완료 처리 실패");
        }

        alert("예매가 완료되었습니다.");

        if (window.history.length > 1) {
            history.back();
        } else {
            location.href = "/reservation";
        }
    } catch (e) {
        console.error(e);
        alert(e.message || "예매 완료 중 오류가 발생했습니다.");
    }
}

async function loadReservationConfirm() {
    const content = document.getElementById("content");

    try {
        const reservationId = getReservationIdFromPath();
        const response = await fetch(`/api/reservation/${reservationId}/confirm`);

        if (!response.ok) {
            const text = await response.text();
            throw new Error(text || "예매 확인 조회 실패");
        }

        const data = await response.json();
        currentReservation = data;
        content.innerHTML = renderDetail(data);
    } catch (e) {
        console.error(e);
        content.innerHTML = `<div class="error-box">예매 정보를 불러오지 못했습니다.<br>${e.message}</div>`;
    }
}

loadReservationConfirm();