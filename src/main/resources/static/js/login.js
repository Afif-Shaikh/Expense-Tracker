document.addEventListener("DOMContentLoaded", function () {
    // Check if already logged in
    fetch("/api/auth/me")
        .then(res => {
            if (res.ok) window.location.href = "Dashboard.html";
        })
        .catch(() => {});

    const form = document.getElementById("login-form");
    const errorMsg = document.getElementById("error-msg");

    form.addEventListener("submit", function (e) {
        e.preventDefault();

        const email = document.getElementById("email").value.trim();
        const password = document.getElementById("password").value;

        if (!email || !password) {
            showError("Please fill in all fields.");
            return;
        }

        fetch("/api/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, password })
        })
        .then(response => {
            if (!response.ok) {
                return response.json().then(err => { throw err; });
            }
            return response.json();
        })
        .then(data => {
            localStorage.setItem("userName", data.name);
            localStorage.setItem("userEmail", data.email);
            window.location.href = "Dashboard.html";
        })
        .catch(error => {
            showError(error.error || "Login failed. Please try again.");
        });
    });

    function showError(msg) {
        errorMsg.textContent = msg;
        errorMsg.style.display = "block";
    }
});