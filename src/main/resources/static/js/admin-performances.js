const adminNameEl = document.getElementById("adminName");
const forbiddenBox = document.getElementById("forbiddenBox");
const pageSection = document.getElementById("pageSection");
const performanceCountEl = document.getElementById("performanceCount");
const performanceTableBody = document.getElementById("performanceTableBody");
const emptyBox = document.getElementById("emptyBox");
const reloadBtn = document.getElementById("reloadBtn");
const searchInput = document.getElementById("searchInput");

let performances = [];

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

function renderPerformances(list) {
    performanceTableBody.innerHTML = "";
    performanceCountEl.textContent = list.length;

    if (!list.length) {
        emptyBox.classList.remove("hidden");
        return;
    }

    emptyBox.classList.add("hidden");

    list.forEach(performance => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
      <td>${performance.performanceId ?? ""}</td>
      <td>${performance.title ?? ""}</td>
      <td>${performance.venue ?? ""}</td>
      <td>${performance.performanceDate ?? ""}</td>
    `;
        performanceTableBody.appendChild(tr);
    });
}

function filterPerformances() {
    const keyword = searchInput.value.trim().toLowerCase();

    const filtered = performances.filter(performance =>
        (performance.title || "").toLowerCase().includes(keyword)
    );

    renderPerformances(filtered);
}

async function loadPage() {
    try {
        const me = await fetchJson("/api/me");

        if (!me || me.role !== "ADMIN_ROLE") {
            showForbidden();
            return;
        }

        adminNameEl.textContent = `${me.loginId}님`;

        performances = await fetchJson("/api/admin/performances");
        renderPerformances(performances);

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
searchInput?.addEventListener("input", filterPerformances);
window.addEventListener("DOMContentLoaded", loadPage);