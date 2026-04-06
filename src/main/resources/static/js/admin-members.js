const adminNameEl = document.getElementById("adminName");
const forbiddenBox = document.getElementById("forbiddenBox");
const pageSection = document.getElementById("pageSection");
const memberCountEl = document.getElementById("memberCount");
const memberTableBody = document.getElementById("memberTableBody");
const emptyBox = document.getElementById("emptyBox");
const reloadBtn = document.getElementById("reloadBtn");
const searchInput = document.getElementById("searchInput");

let members = [];

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

function renderMembers(list) {
    memberTableBody.innerHTML = "";

    memberCountEl.textContent = list.length;

    if (!list.length) {
        emptyBox.classList.remove("hidden");
        return;
    }

    emptyBox.classList.add("hidden");

    list.forEach(member => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
      <td>${member.memberId ?? ""}</td>
      <td>${member.loginId ?? ""}</td>
      <td>${member.role ?? ""}</td>
    `;
        memberTableBody.appendChild(tr);
    });
}

function filterMembers() {
    const keyword = searchInput.value.trim().toLowerCase();

    const filtered = members.filter(member =>
        (member.loginId || "").toLowerCase().includes(keyword)
    );

    renderMembers(filtered);
}

async function loadPage() {
    try {
        const me = await fetchJson("/api/me");

        if (!me || me.role !== "ADMIN_ROLE") {
            showForbidden();
            return;
        }

        adminNameEl.textContent = `${me.loginId}님`;

        members = await fetchJson("/api/admin/members");
        renderMembers(members);

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
searchInput?.addEventListener("input", filterMembers);
window.addEventListener("DOMContentLoaded", loadPage);