const adminNameEl = document.getElementById("adminName");
const forbiddenBox = document.getElementById("forbiddenBox");
const pageSection = document.getElementById("pageSection");
const reloadBtn = document.getElementById("reloadBtn");
const roleText = document.getElementById("roleText");

const totalMembersEl = document.getElementById("totalMembers");
const totalPerformancesEl = document.getElementById("totalPerformances");
const totalReservationsEl = document.getElementById("totalReservations");
const totalPaymentsEl = document.getElementById("totalPayments");

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

async function loadPage() {
    try {
        const me = await fetchJson("/api/me");

        if (!me || me.role !== "ADMIN_ROLE") {
            showForbidden();
            return;
        }

        adminNameEl.textContent = `${me.loginId}님`;
        roleText.textContent = me.role;

        const dashboard = await fetchJson("/api/admin/dashboard");

        totalMembersEl.textContent = dashboard.totalMembers ?? 0;
        totalPerformancesEl.textContent = dashboard.totalPerformances ?? 0;
        totalReservationsEl.textContent = dashboard.totalReservations ?? 0;
        totalPaymentsEl.textContent = dashboard.totalPayments ?? 0;

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
window.addEventListener("DOMContentLoaded", loadPage);