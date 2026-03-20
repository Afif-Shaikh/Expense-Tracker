// Adds logout button and user name to any page header
document.addEventListener("DOMContentLoaded", function () {
    fetch("/api/auth/me")
        .then(res => {
            if (!res.ok) return;
            return res.json();
        })
        .then(user => {
            if (!user) return;

            const header = document.querySelector("header");
            if (!header) return;

            // Check if navbar already added
            if (document.getElementById("user-nav")) return;

            const nav = document.createElement("div");
            nav.id = "user-nav";
            nav.style.cssText = "display:flex; align-items:center; gap:12px;";
            nav.innerHTML = `
                <span style="font-size:14px; color:#555;">
                    Hi, <strong>${user.name}</strong>
                </span>
                <button onclick="doLogout()" style="
                    background-color:#e53935;
                    color:white;
                    padding:8px 16px;
                    font-size:14px;
                    border:none;
                    border-radius:8px;
                    cursor:pointer;
                    text-transform:none;
                    letter-spacing:0;
                ">Logout</button>
            `;
            header.appendChild(nav);
        })
        .catch(() => {});
});

function doLogout() {
    fetch("/api/auth/logout", { method: "POST" })
        .then(() => {
            localStorage.clear();
            window.location.href = "login.html";
        })
        .catch(() => {
            window.location.href = "login.html";
        });
}