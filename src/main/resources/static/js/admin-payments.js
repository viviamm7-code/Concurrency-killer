const adminNameEl = document.getElementById("adminName");
const forbiddenBox = document.getElementById("forbiddenBox");
const pageSection = document.getElementById("pageSection");
const paymentCountEl = document.getElementById("paymentCount");
const paymentTableBody = document.getElementById("paymentTableBody");
const emptyBox = document.getElementById("emptyBox");
const reloadBtn = document.getElementById("reloadBtn");
const searchInput = document.getElementById("searchInput");

let payments = [];

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

function renderPayments(list) {
    paymentTableBody.innerHTML = "";
    paymentCountEl.textContent = list.length;

    if (!list.length) {
        emptyBox.classList.remove("hidden");
        return;
    }

    emptyBox.classList.add("hidden");

    list.forEach(payment => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
      <td>${payment.paymentId ?? ""}</td>
      <td>${payment.reservationId ?? ""}</td>
      <td>${payment.orderId ?? ""}</td>
      <td>${payment.paymentKey ?? ""}</td>
      <td>${payment.amount ?? 0}</td>
    `;
        paymentTableBody.appendChild(tr);
    });
}

function filterPayments() {
    const keyword = searchInput.value.trim().toLowerCase();

    const filtered = payments.filter(payment =>
        (payment.orderId || "").toLowerCase().includes(keyword)
    );

    renderPayments(filtered);
}

async function loadPage() {
    try {
        const me = await fetchJson("/api/me");

        if (!me || me.role !== "ROLE_ROLE") {
            showForbidden();
            return;
        }

        adminNameEl.textContent = `${me.loginId}님`;

        payments = await fetchJson("/api/admin/payments");
        renderPayments(payments);

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
searchInput?.addEventListener("input", filterPayments);
window.addEventListener("DOMContentLoaded", loadPage);