document.addEventListener("DOMContentLoaded", async function () {
    const headerContainer = document.querySelector("#loginButton");
    if (!headerContainer) return;

    const renderGuestButtons = () => {
        headerContainer.innerHTML = `
            <a href="/login" class="loginGradientBtn">로그인</a>
            <a href="#" id="guestMyPageBtn" class="loginGradientBtn">내 정보</a>
        `;

        document.getElementById("guestMyPageBtn")?.addEventListener("click", function (e) {
            e.preventDefault();
            alert("로그인이 필요합니다.");
            location.href = "/login";
        });
    };

    try {
        const response = await fetch('/api/me', {
            credentials: 'include',
            headers: {
                'Accept': 'application/json'
            }
        });

        if (response.status === 401) {
            renderGuestButtons();
            return;
        }

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}`);
        }

        const me = await response.json();
        const isAdmin = me.role === 'ROLE_ADMIN';

        if (isAdmin) {
            headerContainer.innerHTML = `
                <form action="/logout" method="post" style="display:inline;">
                    <button type="submit" class="loginGradientBtn logout-style">로그아웃</button>
                </form>
                <a href="/admin" class="loginGradientBtn">관리자 페이지</a>
            `;
            return;
        }

        headerContainer.innerHTML = `
            <form action="/logout" method="post" style="display:inline;">
                <button type="submit" class="loginGradientBtn logout-style">로그아웃</button>
            </form>
            <a href="/user" class="loginGradientBtn">내 정보</a>
        `;
    } catch (error) {
        console.error("인증 상태 확인 중 오류:", error);
        renderGuestButtons();
    }
});