let reservations = [];
let currentFilter = "ALL";

function formatDateTime(dateTimeStr) {
    if (!dateTimeStr) return "-";

    const date = new Date(dateTimeStr);
    if (isNaN(date)) return dateTimeStr;

    return date.toLocaleString("ko-KR", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit"
    });
}

function getDisplayStatus(item) {
    return item.reservationStatus || "RESERVED";
}

function formatStatus(status) {
    const statusMap = {
        RESERVED: "예매 완료",
        CANCELED: "취소",
        COMPLETED: "관람 완료"
    };

    return statusMap[status] || status || "-";
}

function createCard(item) {
    const seats = item.seatNumbers || [];
    const imageUrl = item.imageUrl || "https://images.unsplash.com/photo-1503095396549-807759245b35?auto=format&fit=crop&w=1200&q=80";

    return `
      <div class="card" onclick="goToDetail(${item.reservationId})" style="cursor:pointer;">
        <img class="poster" src="${imageUrl}" alt="${item.performanceName}" />
        <div class="card-body">
          <div class="badge">${formatStatus(getDisplayStatus(item))}</div>
          <div class="title">${item.performanceName ?? "-"}</div>

          <div class="info-list">
            <div class="info-row">
              <div class="label">공연장</div>
              <div class="value">${item.venue ?? "-"}</div>
            </div>
            <div class="info-row">
              <div class="label">공연일</div>
              <div class="value">${formatDate(item.reservedDate)} ${formatTime(item.startedAt)}</div>
            </div>
            <div class="info-row">
              <div class="label">예매일</div>
              <div class="value">${formatDateTime(item.reservedAt)}</div>
            </div>
          </div>

          <div class="seat-box">
            <div class="seat-title">예매 좌석</div>
            <div class="seat-list">
              ${
        seats.length > 0
            ? seats.map(seat => `<span class="seat-item">${seat}</span>`).join("")
            : `<span class="seat-item">좌석 정보 없음</span>`
    }
            </div>
          </div>
        </div>
      </div>
      
    `;
}


function renderSection(title, items) {
    return `
    <div class="status-section">
      <div class="status-title">${title}</div>
      ${
        items && items.length > 0
            ? `
            <div class="card-grid">
              ${items.map(item => createCard(item)).join("")}
            </div>
          `
            : `
            <div class="message-box">해당 상태의 예매 내역이 없습니다.</div>
          `
    }
    </div>
  `;
}
function formatDate(value) {
    if (!value) return "-";

    const date = new Date(value);
    if (!isNaN(date)) {
        return date.toLocaleDateString("ko-KR", {
            year: "numeric",
            month: "2-digit",
            day: "2-digit"
        });
    }

    const match = String(value).match(/^\d{4}-\d{2}-\d{2}/);
    return match ? match[0] : String(value);
}

function formatTime(value) {
    if (!value) return "-";

    if (/^\d{2}:\d{2}$/.test(String(value))) {
        return String(value);
    }

    const match = String(value).match(/(\d{2}:\d{2})/);
    return match ? match[1] : "-";
}

function goToDetail(reservationId) {
    location.href = `/reservation/${reservationId}`;
}

async function loadReservations() {
    const content = document.getElementById("content");

    content.innerHTML = `<div class="message-box">예매 내역을 불러오는 중입니다...</div>`;

    try {
        const response = await fetch("/api/reservation");
        const data = await response.json();

        reservations = data || [];
        renderReservations();
    } catch (error) {
        console.error(error);
        content.innerHTML = `<div class="message-box">예매 내역을 불러오지 못했습니다.</div>`;
        document.getElementById("count").textContent = 0;
    }
}

function changeFilter(filter, label) {
    currentFilter = filter;
    document.getElementById("currentFilterLabel").textContent = `${label} ▾`;
    renderReservations();
}

function renderReservations() {
    const content = document.getElementById("content");
    const count = document.getElementById("count");

    if (!reservations || reservations.length === 0) {
        count.textContent = 0;
        content.innerHTML = `<div class="message-box">예매한 공연이 없습니다.</div>`;
        return;
    }

    if (currentFilter === "ALL") {
        const reservedItems = reservations.filter(item => getDisplayStatus(item) === "RESERVED");
        const canceledItems = reservations.filter(item => getDisplayStatus(item) === "CANCELED");
        const completedItems = reservations.filter(item => getDisplayStatus(item) === "COMPLETED");

        count.textContent = reservations.length;

        content.innerHTML = `
      ${renderSection("예매 완료", reservedItems)}
      ${renderSection("취소", canceledItems)}
      ${renderSection("관람 완료", completedItems)}
     
    `;

        return;
    }

    const filteredData = reservations.filter(item => getDisplayStatus(item) === currentFilter);

    count.textContent = filteredData.length;

    if (!filteredData.length) {
        content.innerHTML = `<div class="message-box">해당 조건의 예매 내역이 없습니다.</div>`;
        return;
    }

    content.innerHTML = renderSection(formatStatus(currentFilter), filteredData);
}

loadReservations();

window.addEventListener("pageshow", function () {
    const updateRaw = sessionStorage.getItem("reservationUpdate");
    if (!updateRaw) return;

    // 목록 데이터가 살아있을 때만 로컬 반영
    if (!reservations || reservations.length === 0) {
        sessionStorage.removeItem("reservationUpdate");
        return;
    }

    const update = JSON.parse(updateRaw);

    reservations = reservations.map(item =>
        item.reservationId === update.reservationId
            ? {
                ...item,
                reservationStatus: update.reservationStatus
            }
            : item
    );

    sessionStorage.removeItem("reservationUpdate");
    renderReservations();
});