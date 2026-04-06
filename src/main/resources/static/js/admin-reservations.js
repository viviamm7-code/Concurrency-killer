const reservationSearchInput = document.getElementById("reservationSearchInput");
const reservationTableBody = document.getElementById("reservationTableBody");
const reservationCount = document.getElementById("reservationCount");
const reservationEmptyBox = document.getElementById("reservationEmptyBox");
const reservationErrorBox = document.getElementById("reservationErrorBox");

let debounceTimer = null;

document.addEventListener("DOMContentLoaded", () => {
    fetchReservations();
});

reservationSearchInput.addEventListener("input", () => {
    clearTimeout(debounceTimer);

    debounceTimer = setTimeout(() => {
        fetchReservations(reservationSearchInput.value.trim());
    }, 300);
});

async function fetchReservations(keyword = "") {
    try {
        hideMessageBoxes();
        reservationTableBody.innerHTML = "";

        const url = keyword
            ? `/api/admin/reservations?keyword=${encodeURIComponent(keyword)}`
            : `/api/admin/reservations`;

        const response = await fetch(url, {
            method: "GET",
            credentials: "include",
            headers: {
                "Accept": "application/json"
            }
        });

        if (!response.ok) {
            throw new Error(`예매 목록 조회 실패: ${response.status}`);
        }

        const reservations = await response.json();
        renderReservations(reservations);
    } catch (error) {
        console.error(error);
        reservationCount.textContent = "0";
        reservationErrorBox.classList.remove("hidden");
    }
}

function renderReservations(reservations) {
    reservationTableBody.innerHTML = "";
    reservationCount.textContent = reservations.length;

    if (!reservations || reservations.length === 0) {
        reservationEmptyBox.classList.remove("hidden");
        return;
    }

    reservations.forEach(reservation => {
        const row = document.createElement("tr");

        row.innerHTML = `
            <td>${reservation.reservationId ?? ""}</td>
            <td>${escapeHtml(reservation.username ?? "")}</td>
            <td>${escapeHtml(reservation.memberName ?? "")}</td>
            <td>${escapeHtml(reservation.performanceTitle ?? "")}</td>
         
            <td>${escapeHtml(reservation.seatInfo ?? "")}</td>
            <td>${renderStatusBadge(reservation.reservationStatus)}</td>
          
            
        `;

        reservationTableBody.appendChild(row);
    });
}

function renderStatusBadge(status) {
    if (!status) {
        return `<span class="status-badge">-</span>`;
    }

    if (status === "RESERVED") {
        return `<span class="status-badge status-reserved">예매 완료</span>`;
    }

    if (status === "CANCELED") {
        return `<span class="status-badge status-canceled">취소</span>`;
    }

    if (status === "COMPLETED") {
        return `<span class="status-badge status-completed">관람 완료</span>`;
    }

    return `<span class="status-badge">${escapeHtml(status)}</span>`;
}

function formatPrice(value) {
    if (value === null || value === undefined) return "-";
    return `${Number(value).toLocaleString("ko-KR")}원`;
}

function formatDateTime(value) {
    if (!value) return "-";

    const date = new Date(value);

    if (isNaN(date.getTime())) {
        return value;
    }

    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    const hours = String(date.getHours()).padStart(2, "0");
    const minutes = String(date.getMinutes()).padStart(2, "0");

    return `${year}-${month}-${day} ${hours}:${minutes}`;
}

function hideMessageBoxes() {
    reservationEmptyBox.classList.add("hidden");
    reservationErrorBox.classList.add("hidden");
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#39;");
}