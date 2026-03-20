document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("register-form");
    const errorMsg = document.getElementById("error-msg");

    form.addEventListener("submit", function (e) {
        e.preventDefault();

        const name = document.getElementById("name").value.trim();
        const email = document.getElementById("email").value.trim();
        const password = document.getElementById("password").value;
        const confirmPassword = document.getElementById("confirm-password").value;

        if (!name || !email || !password || !confirmPassword) {
            showError("Please fill in all fields.");
            return;
        }

        if (password.length < 6) {
            showError("Password must be at least 6 characters.");
            return;
        }

        if (password !== confirmPassword) {
            showError("Passwords do not match.");
            return;
        }

        fetch("/api/auth/register", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ name, email, password })
        })
        .then(response => {
            if (!response.ok) {
                return response.json().then(err => { throw err; });
            }
            return response.json();
        })
        .then(data => {
            alert("Registration successful! Please login.");
            window.location.href = "login.html";
        })
        .catch(error => {
            showError(error.error || "Registration failed. Please try again.");
        });
    });

    function showError(msg) {
        errorMsg.textContent = msg;
        errorMsg.style.display = "block";
    }
});