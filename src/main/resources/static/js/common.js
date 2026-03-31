document.addEventListener("DOMContentLoaded", async function() {
    const headerContainer = document.querySelector("#loginButton");

    if (headerContainer) {
        try {
            // 1. 서버에 로그인 상태 확인 요청
            const response = await fetch('/api/auth/status');
            const auth = await response.json();

            // 2. 상태에 따라 다른 HTML 주입
            if (auth.isLoggedIn) {
                // 로그인 된 상태 -> 로그아웃 버튼과 내 예매 버튼
                headerContainer.innerHTML = `
                    <a href="/logout1" class="loginGradientBtn logout-style">로그아웃</a>
                    <a href="/reservation" class="myPage">내 예매</a>
                `;
            } else {
                // 로그인 안 된 상태 -> 로그인 버튼과 내 예매 버튼(클릭 시 컨트롤러에서 튕김)
                headerContainer.innerHTML = `
                    <a href="/login" class="loginGradientBtn">로그인</a>
                    <a href="/login" class="myPage">내 예매</a>
                `;
            }
        } catch (error) {
            console.error("인증 상태 확인 중 오류:", error);
        }
    }
});