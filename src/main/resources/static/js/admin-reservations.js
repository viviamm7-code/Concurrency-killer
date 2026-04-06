const adminNameEl = document.getElementById("adminName");
const forbiddenBox = document.getElementById("forbiddenBox");
const pageSection = document.getElementById("pageSection");
const reservationCountEl = document.getElementById("reservationCount");
const reservationTableBody = document.getElementById("reservationTableBody");
const emptyBox = document.getElementById("emptyBox");
const reloadBtn = document.getElementById("reloadBtn");
const searchInput = document.getElementById("searchInput");

let reservations = [];

async function fetchJson(url) {
    const response = await fetch(url, {
        method: "GET",
        credentials: "include"
    });

    if (response.status === 401 || response.status === 403) {
        throw new Error("FORBIDDEN");
    }

    if (!response.ok) {
        throw new Error("API_ERROR");
    }

    return response.json();
}

function getStatusBadge(status) {
    if (status === "RESERVED") {
        return `<span class="status-badge status-reserved">예매 완료</span>`;
    }
    if (status === "CANCELED") {
        return `<span class="status-badge status-canceled">취소</span>`;
    }
    if (status === "COMPLETED") {
        return `<span class="status-badge status-completed">관람 완료</span>`;
    }
    return `<span class="status-badge">${status ?? ""}</span>`;
}

function renderReservations(list) {
    reservationTableBody.innerHTML = "";
    reservationCountEl.textContent = list.length;

    if (!list.length) {
        emptyBox.classList.remove("hidden");
        return;
    }

    emptyBox.classList.add("hidden");

    list.forEach(reservation => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
      <td>${reservation.reservationId ?? ""}</td>
      <td>${reservation.loginId ?? ""}</td>
      <td>${reservation.performanceTitle ?? ""}</td>
      <td>${getStatusBadge(reservation.reservationStatus)}</td>
      <td>${reservation.totalPrice ?? 0}</td>
    `;
        reservationTableBody.appendChild(tr);
    });
}

function filterReservations() {
    const keyword = searchInput.value.trim().toLowerCase();

    const filtered = reservations.filter(reservation =>
        (reservation.loginId || "").toLowerCase().includes(keyword) ||
        (reservation.performanceTitle || "").toLowerCase().includes(keyword)
    );

    renderReservations(filtered);
}

async function loadPage() {
    try {
        const me = await fetchJson("/api/me");

        if (!me || me.role !== "ADMIN_ROLE") {
            showForbidden();
            return;
        }

        adminNameEl.textContent = `${me.loginId}님`;

        reservations = await fetchJson("/api/admin/reservations");
        renderReservations(reservations);

        pageSection.classList.remove("hidden");
        forbiddenBox.classList.add("hidden");
    } catch (error) {
        showForbidden();
    }
}

function showForbidden() {
    pageSection.classList.add("hidden");
    forbiddenBox.classList.remove("hidden");
    adminNameEl.textContent = "접근 불가";
}

reloadBtn?.addEventListener("click", loadPage);
searchInput?.addEventListener("input", filterReservations);
window.addEventListener("DOMContentLoaded", loadPage);