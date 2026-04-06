document.addEventListener("DOMContentLoaded", async function () {
    const loadingBox = document.getElementById("loadingBox");
    const errorBox = document.getElementById("errorBox");
    const profileSection = document.getElementById("profileSection");
    const passwordSection = document.getElementById("passwordSection");
    const passwordForm = document.getElementById("passwordForm");
    const dangerSection = document.getElementById("dangerSection");

    try {
        const response = await fetch("/api/mypage", {
            credentials: "include",
            headers: {
                "Accept": "application/json"
            }
        });

        if (response.status === 401) {
            alert("로그인이 필요합니다.");
            location.href = "/login";
            return;
        }

        if (!response.ok) {
            throw new Error("회원 정보를 불러오지 못했습니다.");
        }

        const data = await response.json();

        document.getElementById("profileName").textContent = data.name || "-";
        document.getElementById("profileRole").textContent =
            data.role === "ROLE_ADMIN" ? "관리자 계정" : "일반 회원";

        document.getElementById("username").textContent = data.username ?? "-";
        document.getElementById("name").textContent = data.name ?? "-";
        document.getElementById("email").textContent = data.email ?? "-";
        document.getElementById("createdAt").textContent = data.createdAt ?? "-";

        loadingBox.classList.add("hidden");
        errorBox.classList.add("hidden");
        profileSection.classList.remove("hidden");
        passwordSection.classList.remove("hidden");
        dangerSection.classList.remove("hidden");

    } catch (error) {
        console.error(error);
        loadingBox.classList.add("hidden");
        profileSection.classList.add("hidden");
        passwordSection.classList.add("hidden");
        dangerSection.classList.add("hidden");
        errorBox.classList.remove("hidden");
    }

    passwordForm.addEventListener("submit", async function (e) {
        e.preventDefault();

        const currentPassword = document.getElementById("currentPassword").value.trim();
        const newPassword = document.getElementById("newPassword").value.trim();
        const newPasswordConfirm = document.getElementById("newPasswordConfirm").value.trim();

        if (!currentPassword) {
            alert("현재 비밀번호를 입력해주세요.");
            return;
        }

        if (!newPassword) {
            alert("새 비밀번호를 입력해주세요.");
            return;
        }

        if (!newPasswordConfirm) {
            alert("새 비밀번호 확인을 입력해주세요.");
            return;
        }

        try {
            const response = await fetch("/api/mypage/password", {
                method: "PATCH",
                credentials: "include",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    currentPassword,
                    newPassword,
                    newPasswordConfirm
                })
            });

            const contentType = response.headers.get("content-type") || "";

            if (!response.ok) {
                if (contentType.includes("application/json")) {
                    const errorData = await response.json();
                    throw new Error(errorData.message || "비밀번호 변경에 실패했습니다.");
                } else {
                    const errorText = await response.text();
                    throw new Error(errorText || "비밀번호 변경에 실패했습니다.");
                }
            }

            alert("비밀번호가 변경되었습니다.");
            passwordForm.reset();
        } catch (error) {
            console.error(error);
            alert(error.message || "비밀번호 변경 중 오류가 발생했습니다.");
        }
    });
});
const deleteMyAccountBtn = document.getElementById("deleteMyAccountBtn");

if (deleteMyAccountBtn) {
    deleteMyAccountBtn.addEventListener("click", async () => {
        const confirmed = confirm("정말 회원 탈퇴하시겠습니까?");
        if (!confirmed) return;

        try {
            const response = await fetch("/api/mypage/delete", {
                method: "DELETE",
                credentials: "include",
                headers: {
                    "Accept": "application/json"
                }
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || "회원 탈퇴 실패");
            }

            alert("회원 탈퇴가 완료되었습니다.");
            location.href = "/";
        } catch (error) {
            console.error(error);
            alert("회원 탈퇴 중 오류가 발생했습니다.");
        }
    });
}
