const fallbackImage = "https://images.unsplash.com/photo-1503095396549-807759245b35?auto=format&fit=crop&w=1200&q=80";

function getReservationIdFromPath() {
    const pathParts = window.location.pathname.split("/");
    const value = pathParts[pathParts.length - 1];

    if (!/^\d+$/.test(value)) {
        throw new Error("잘못된 reservationId: " + value);
    }

    return value;
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

function formatStatus(status) {
    const map = {
        RESERVED: "예매 완료",
        CANCELED: "취소",
        COMPLETED: "관람 완료"
    };
    return map[status] || status || "-";
}

function renderCancelButtons(type) {
    const buttonBox = document.getElementById("cancelActionButtons");

    if (type === "close") {
        buttonBox.innerHTML = `
                <button class="modal-btn no" onclick="closeCancelModal()">닫기</button>
            `;
        return;
    }

    if (type === "confirm") {
        buttonBox.innerHTML = `
                <button class="modal-btn no" onclick="closeCancelModal()">아니오</button>
                <button class="modal-btn yes" onclick="confirmCancel()">예</button>
            `;
        return;
    }

    buttonBox.innerHTML = `
            <button class="modal-btn no" onclick="closeCancelModal()">아니오</button>
            <button class="modal-btn yes" disabled>예</button>
        `;
}

function renderDetail(data) {
    const seats = data.seatNumbers || [];
    const imageUrl = data.imageUrl || fallbackImage;
    const displayStatus = getDisplayStatus(data);
    const isCanceled = displayStatus === "CANCELED";
    const isCompleted = displayStatus === "COMPLETED";
    const canComplete = data.reservationStatus === "RESERVED" && isCompleted;

    return `
        <div class="detail-card">
            <img class="poster" src="${imageUrl}" alt="${data.performanceName}">
            <div class="content-box">
                <div class="badge">${formatStatus(displayStatus)}</div>
                <div class="title">${data.performanceName}</div>

                <div class="info-grid">
                    <div class="info-item">
                        <div class="info-label">예매 번호</div>
                        <div class="info-value">${data.reservationId}</div>
                    </div>
                    <div class="info-item">
                        <div class="info-label">이름</div>
                        <div class="info-value">${data.reservationName || "-"}</div>
                    </div>
                    <div class="info-item">
                        <div class="info-label">공연장</div>
                        <div class="info-value">${data.venue}</div>
                    </div>
                    <div class="info-item">
                        <div class="info-label">공연일시</div>
                        <div class="info-value">${formatDateTime(data.startedAt)}</div>
                    </div>
                    <div class="info-item">
                        <div class="info-label">예매일시</div>
                        <div class="info-value">${formatDateTime(data.reservedAt)}</div>
                    </div>
                    <div class="info-item">
                        <div class="info-label">가격</div>
                        <div class="info-value">${formatPrice(data.price)}</div>
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

                ${
        isCanceled
            ? `<button class="cancel-btn" disabled>이미 취소된 예매입니다</button>`
            : canComplete
                ? `<button class="cancel-btn" disabled>관람 완료 처리</button>`
                : isCompleted
                    ? `<button class="cancel-btn" disabled>관람이 완료된 예매입니다</button>`
                    : `<button class="cancel-btn" onclick="openCancelModal()">예매 취소</button>`
    }
            </div>
        </div>
    `;
}

function openCancelModal() {
    document.getElementById("cancelModal").style.display = "flex";
    document.getElementById("refundInfoText").innerHTML = "환불 정보를 계산 중입니다...";
    document.getElementById("cancelQuestion").textContent = "예매를 취소하시겠습니까?";
    renderCancelButtons("loading");
    loadCancelPreview();
}

function closeCancelModal() {
    document.getElementById("cancelModal").style.display = "none";
}

function getDisplayStatus(data) {
    if (data.reservationStatus === "CANCELED") {
        return "CANCELED";
    }

    if (data.reservationStatus === "COMPLETED") {
        return "COMPLETED";
    }

    const startedAt = new Date(data.startedAt);
    const now = new Date();

    if (!isNaN(startedAt) && startedAt < now) {
        return "COMPLETED";
    }

    return "RESERVED";
}

async function loadCancelPreview() {
    const reservationId = getReservationIdFromPath();

    try {
        const response = await fetch(`/api/reservation/${reservationId}/cancel-preview`);

        if (!response.ok) {
            const text = await response.text();
            throw new Error(text);
        }

        const data = await response.json();

        document.getElementById("refundInfoText").innerHTML = `
                총 결제 금액: ${formatPrice(data.totalPrice)}<br>
                환불 비율: ${data.refundRate}%<br>
                환불 금액: ${formatPrice(data.refundAmount)}<br>
                안내: ${data.message || "-"}
            `;

        if (data.reservationStatus === "CANCELED") {
            document.getElementById("cancelQuestion").textContent = "이미 취소된 예매입니다.";
            renderCancelButtons("close");
            return;
        }

        if (data.refundRate === 0) {
            document.getElementById("cancelQuestion").textContent = data.message || "취소할 수 없는 예매입니다.";
            renderCancelButtons("close");
            return;
        }

        renderCancelButtons("confirm");
    } catch (e) {
        console.error(e);
        document.getElementById("refundInfoText").innerHTML = `환불 정보를 불러오지 못했습니다.<br>${e.message}`;
        document.getElementById("cancelQuestion").textContent = "취소 정보를 확인할 수 없습니다.";
        renderCancelButtons("close");
    }
}

function goBackAfterCancel() {
    if (window.history.length > 1) {
        history.back();
    } else {
        location.href = "/reservation";
    }
}

async function confirmCancel() {
    const reservationId = getReservationIdFromPath();

    try {
        const response = await fetch(`/api/reservation/${reservationId}/cancel`, {
            method: "POST"
        });

        if (!response.ok) {
            const text = await response.text();
            throw new Error(text);
        }

        const result = await response.json();

        document.getElementById("refundInfoText").innerHTML = `
            총 결제 금액: ${formatPrice(result.totalPrice)}<br>
            환불 비율: ${result.refundRate}%<br>
            환불 금액: ${formatPrice(result.refundAmount)}<br>
            안내: ${result.message || "-"}
        `;

        document.getElementById("cancelQuestion").textContent = "환불 처리가 완료되었습니다.";

        document.getElementById("cancelActionButtons").innerHTML = `
            <button class="modal-btn yes" onclick="goBackAfterCancel()">확인</button>
        `;

        sessionStorage.setItem("reservationUpdate", JSON.stringify({
            reservationId: Number(reservationId),
            reservationStatus: "CANCELED",
            seatNumbers: []
        }));

        // 1.2초 뒤 자동 이동
        setTimeout(() => {
            goBackAfterCancel();
        }, 1200);

    } catch (e) {
        console.error(e);
        document.getElementById("refundInfoText").innerHTML = `예매 취소에 실패했습니다.<br>${e.message}`;
        document.getElementById("cancelQuestion").textContent = "다시 시도해주세요.";
        renderCancelButtons("close");
    }
}

async function loadReservationDetail() {
    const content = document.getElementById("content");


    try {
        const reservationId = getReservationIdFromPath();
        const response = await fetch(`/api/reservation/${reservationId}/detail`);

        if (!response.ok) {
            const text = await response.text();
            throw new Error(`API 오류: ${response.status} / ${text}`);
        }

        const data = await response.json();
        console.log("detail api data =", data);
        console.log("reservationStatus =", data.reservationStatus);
        content.innerHTML = renderDetail(data);

    } catch (e) {
        console.error(e);
        content.innerHTML = `
                <div class="error-box">
                    예매 상세 정보를 불러오지 못했습니다.<br>
                    ${e.message}
                </div>
            `;
    }
}

window.onload = loadReservationDetail;