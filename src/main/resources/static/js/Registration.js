document.getElementById("registerForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const name = document.getElementById("name").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    try {
        const response = await fetch("http://localhost:8080/api/auth/register", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ name, email, password }),
        });

        if (response.ok) {
            alert("Registration successful! Please login.");
            window.location.href = "login.html"; // ✅ Redirect to login page
        } else {
            alert("Email already exists. Try another.");
        }
    } catch (error) {
        console.error("Registration Error:", error);
        alert("Something went wrong. Try again.");
    }
});



/*// Registration form validation
document.getElementById('register-form').addEventListener('submit', function(event) {
    event.preventDefault(); // Prevent form submission

    const username = document.getElementById('username').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirm-password').value;

    // Basic validation
    if (username === '' || email === '' || password === '' || confirmPassword === '') {
        alert('All fields are required!');
        return;
    }

    if (password !== confirmPassword) {
        alert('Passwords do not match!');
        return;
    }

    // Simulating successful registration (you can store this data in a database)
    alert('Registration Successful! You can now login.');
    window.location.href = 'login.html'; // Redirect to the login page after successful registration
});
*/