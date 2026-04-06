const adminName = document.getElementById("adminName");
const forbiddenBox = document.getElementById("forbiddenBox");
const dashboardSection = document.getElementById("dashboardSection");
const reloadBtn = document.getElementById("reloadBtn");

const totalMembers = document.getElementById("totalMembers");
const totalReservations = document.getElementById("totalReservations");
const totalPayments = document.getElementById("totalPayments");

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

async function loadAdminPage() {
    try {
        const me = await fetchJson("/api/me");

        if (!me || me.role !== "ROLE_ADMIN") {
            showForbidden();
            return;
        }

        const dashboard = await fetchJson("/api/admin");

        totalMembers.textContent = dashboard.totalMembers ?? 0;
        totalReservations.textContent = dashboard.totalReservations ?? 0;
        totalPayments.textContent = dashboard.totalPayments ?? 0;

        dashboardSection.classList.remove("hidden");
        forbiddenBox.classList.add("hidden");
    } catch (error) {
        showForbidden();
    }
}

function showForbidden() {
    dashboardSection.classList.add("hidden");
    forbiddenBox.classList.remove("hidden");
    adminName.textContent = "접근 불가";
}

reloadBtn?.addEventListener("click", loadAdminPage);

window.addEventListener("DOMContentLoaded", loadAdminPage);