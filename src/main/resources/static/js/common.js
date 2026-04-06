document.addEventListener("DOMContentLoaded", async function () {
    const headerContainer = document.querySelector("#loginButton");
    if (!headerContainer) return;

    try {
        const response = await fetch('/api/me', {
            credentials: 'include',
            headers: {
                'Accept': 'application/json'
            }
        });

        if (response.status === 401) {
            headerContainer.innerHTML = `
                <a href="/login" class="loginGradientBtn">로그인</a>
                <a href="/login" class="loginGradientBtn">내 예매</a>
            `;
            return;
        }

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}`);
        }

        const me = await response.json();
        const isAdmin = me.role === 'ROLE_ADMIN';

        headerContainer.innerHTML = `
            <form action="/logout" method="post" style="display:inline;">
                <button type="submit" class="loginGradientBtn logout-style">로그아웃</button>
            </form>
            <a href="${isAdmin ? '/admin' : '/user'}" class="loginGradientBtn">
                ${isAdmin ? '관리자 페이지' : '내 정보'}
            </a>
        `;
    } catch (error) {
        console.error("인증 상태 확인 중 오류:", error);
        headerContainer.innerHTML = `
            <a href="/login" class="loginGradientBtn">로그인</a>
            <a href="/login" class="loginGradientBtn">내 정보</a>
        `;
    }
});