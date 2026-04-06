const searchInput = document.getElementById("memberSearchInput");
const memberTableBody = document.getElementById("memberTableBody");
const memberCount = document.getElementById("memberCount");
const memberEmptyBox = document.getElementById("memberEmptyBox");
const memberErrorBox = document.getElementById("memberErrorBox");

let debounceTimer = null;
let openedMemberId = null;

document.addEventListener("DOMContentLoaded", () => {
    fetchMembers();
});

searchInput.addEventListener("input", () => {
    clearTimeout(debounceTimer);

    debounceTimer = setTimeout(() => {
        fetchMembers(searchInput.value.trim());
    }, 300);
});

async function fetchMembers(keyword = "") {
    try {
        hideMessageBoxes();
        openedMemberId = null;
        memberTableBody.innerHTML = "";

        const url = keyword
            ? `/api/admin/members?keyword=${encodeURIComponent(keyword)}`
            : `/api/admin/members`;

        const response = await fetch(url, {
            method: "GET",
            credentials: "include",
            headers: {
                "Accept": "application/json"
            }
        });

        if (!response.ok) {
            throw new Error(`회원 목록 조회 실패: ${response.status}`);
        }

        const members = await response.json();
        renderMembers(members);
    } catch (error) {
        console.error(error);
        memberCount.textContent = "0";
        memberErrorBox.classList.remove("hidden");
    }
}

function renderMembers(members) {
    memberTableBody.innerHTML = "";
    memberCount.textContent = members.length;

    if (!members || members.length === 0) {
        memberEmptyBox.classList.remove("hidden");
        return;
    }

    members.forEach(member => {
        const row = document.createElement("tr");
        row.dataset.memberId = member.id;
        row.style.cursor = "pointer";

        row.innerHTML = `
            <td style="text-align: right; padding-right: 50px;">${member.id}</td>
            <td style="text-align: center;">${escapeHtml(member.username)}</td>
        `;

        row.addEventListener("click", async () => {
            await toggleMemberDetail(member.id, row);
        });

        memberTableBody.appendChild(row);
    });
}

async function toggleMemberDetail(memberId, clickedRow) {
    const existingDetailRow = document.querySelector(".member-detail-row");
    const activeRows = document.querySelectorAll("#memberTableBody tr");

    if (openedMemberId === memberId) {
        if (existingDetailRow) {
            existingDetailRow.remove();
        }
        openedMemberId = null;
        return;
    }

    if (existingDetailRow) {
        existingDetailRow.remove();
    }

    activeRows.forEach(row => {
        row.style.background = "";
    });

    try {
        const response = await fetch(`/api/admin/members/${memberId}`, {
            method: "GET",
            credentials: "include",
            headers: {
                "Accept": "application/json"
            }
        });

        if (!response.ok) {
            throw new Error(`회원 상세 조회 실패: ${response.status}`);
        }

        const member = await response.json();

        const detailRow = document.createElement("tr");
        detailRow.className = "member-detail-row";

        detailRow.innerHTML = `
    <td colspan="2">
        <div style="
            padding: 22px 18px;
            background: rgba(255,255,255,0.03);
            border-top: 1px solid rgba(255,255,255,0.06);
        ">
            <div style="
                font-size: 18px;
                font-weight: 800;
                margin-bottom: 16px;
                color: #f8fafc;
            ">
                회원 상세 정보
            </div>

            <div style="
                display: grid;
                grid-template-columns: 140px 1fr;
                gap: 12px 20px;
                margin-bottom: 20px;
            ">
                <div style="color:#94a3b8; font-size:14px; font-weight:700;">회원번호</div>
                <div style="color:#f8fafc; font-size:14px;">${member.id}</div>

                <div style="color:#94a3b8; font-size:14px; font-weight:700;">아이디</div>
                <div style="color:#f8fafc; font-size:14px;">${escapeHtml(member.username ?? "")}</div>

                <div style="color:#94a3b8; font-size:14px; font-weight:700;">이메일</div>
                <div style="color:#f8fafc; font-size:14px; word-break:break-all;">
                    ${escapeHtml(member.email ?? "")}
                </div>

                <div style="color:#94a3b8; font-size:14px; font-weight:700;">권한</div>
                <div style="color:#f8fafc; font-size:14px;">${escapeHtml(member.role ?? "")}</div>

                <div style="color:#94a3b8; font-size:14px; font-weight:700;">가입일</div>
                <div style="color:#f8fafc; font-size:14px;">${formatDateTime(member.createdAt)}</div>
                
                ${
            member.role === "ROLE_ADMIN"
                ? ``
                : `<button onclick="deleteMember(${member.id})" style="
            border:none;
            background:linear-gradient(135deg, #ef4444, #dc2626);
            color:white;
            padding:12px 18px;
            border-radius:14px;
            font-size:14px;
            font-weight:700;
            cursor:pointer;
        ">회원 삭제</button>`
        }
            </div>
        </div>
    </td>
`;

        clickedRow.style.background = "rgba(255,255,255,0.03)";
        clickedRow.insertAdjacentElement("afterend", detailRow);
        openedMemberId = memberId;
    } catch (error) {
        console.error(error);
        memberErrorBox.classList.remove("hidden");
    }
}

async function deleteMember(memberId) {
    const confirmed = confirm("정말 이 회원과 기록을 모두 삭제하시겠습니까?");
    if (!confirmed) return;

    try {
        const response = await fetch(`/api/admin/members/${memberId}/delete`, {
            method: "DELETE",
            credentials: "include"
        });

        if (!response.ok) {
            throw new Error("회원 삭제 실패");
        }

        alert("회원이 삭제되었습니다.");
        fetchMembers(searchInput.value.trim());
    } catch (error) {
        console.error(error);
        alert("회원 삭제 중 오류가 발생했습니다.");
    }
}

function hideMessageBoxes() {
    memberEmptyBox.classList.add("hidden");
    memberErrorBox.classList.add("hidden");
}

function formatDateTime(value) {
    if (!value) return "";

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

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#39;");
}