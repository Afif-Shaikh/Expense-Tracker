// Include this in every protected page BEFORE other scripts
(function () {
    fetch("/api/auth/me")
        .then(response => {
            if (!response.ok) {
                window.location.href = "login.html";
                throw new Error("Not authenticated");
            }
            return response.json();
        })
        .then(user => {
            // Store user info for use by other scripts
            window.currentUser = user;
        })
        .catch(() => {
            window.location.href = "login.html";
        });
})();