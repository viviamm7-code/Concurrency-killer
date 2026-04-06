const paymentSearchInput = document.getElementById("paymentSearchInput");
const paymentTableBody = document.getElementById("paymentTableBody");
const paymentCount = document.getElementById("paymentCount");
const paymentEmptyBox = document.getElementById("paymentEmptyBox");
const paymentErrorBox = document.getElementById("paymentErrorBox");

let debounceTimer = null;

document.addEventListener("DOMContentLoaded", () => {
    fetchPayments();
});

paymentSearchInput.addEventListener("input", () => {
    clearTimeout(debounceTimer);

    debounceTimer = setTimeout(() => {
        fetchPayments(paymentSearchInput.value.trim());
    }, 300);
});

async function fetchPayments(keyword = "") {
    try {
        hideMessageBoxes();
        paymentTableBody.innerHTML = "";

        const url = keyword
            ? `/api/admin/payments?keyword=${encodeURIComponent(keyword)}`
            : `/api/admin/payments`;

        const response = await fetch(url, {
            method: "GET",
            credentials: "include",
            headers: {
                "Accept": "application/json"
            }
        });

        if (!response.ok) {
            throw new Error(`결제 목록 조회 실패: ${response.status}`);
        }

        const payments = await response.json();
        renderPayments(payments);
    } catch (error) {
        console.error(error);
        paymentCount.textContent = "0";
        paymentErrorBox.classList.remove("hidden");
    }
}

function renderPayments(payments) {
    paymentTableBody.innerHTML = "";
    paymentCount.textContent = payments.length;

    if (!payments || payments.length === 0) {
        paymentEmptyBox.classList.remove("hidden");
        return;
    }

    payments.forEach(payment => {
        const row = document.createElement("tr");

        row.innerHTML = `
            <td>${payment.paymentId ?? ""}</td>
            <td>${escapeHtml(payment.username ?? "")}</td>
            <td>${escapeHtml(payment.memberName ?? "")}</td>
            <td>${escapeHtml(payment.performanceTitle ?? "")}</td>
            <td>${formatPrice(payment.amount)}</td>
            <td>${escapeHtml(payment.orderId ?? "-")}</td>
            <td>${formatDateTime(payment.paidAt)}</td>
        `;

        paymentTableBody.appendChild(row);
    });
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
    paymentEmptyBox.classList.add("hidden");
    paymentErrorBox.classList.add("hidden");
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#39;");
}