/*// Login form validation
document.getElementById('login-form').addEventListener('submit', function(event) {
    event.preventDefault(); // Prevent form submission

    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;*/
	
	document.getElementById("loginForm").addEventListener("submit", async function (e) {
	    e.preventDefault();

	    const email = document.getElementById("email").value;
	    const password = document.getElementById("password").value;

	    try {
	        const response = await fetch("http://localhost:8080/api/auth/login", {
	            method: "POST",
	            headers: { "Content-Type": "application/json" },
	            body: JSON.stringify({ email, password }),
	        });

	        if (response.ok) {
	            const userData = await response.json();
	            localStorage.setItem("user", JSON.stringify(userData)); // ✅ Save user data
	            window.location.href = "Dashboard.html"; // ✅ Redirect after login
	        } else {
	            alert("Invalid email or password");
	        }
	    } catch (error) {
	        console.error("Login Error:", error);
	        alert("Something went wrong. Try again.");
	    }
	});

	// ✅ Google Login: Fetch user details if already authenticated
	async function checkGoogleLogin() {
	    try {
	        const response = await fetch("http://localhost:8080/user");
	        if (response.ok) {
	            const userData = await response.json();
	            localStorage.setItem("user", JSON.stringify(userData)); // ✅ Save user
	            window.location.href = "Dashboard.html"; // ✅ Redirect
	        }
	    } catch (error) {
	        console.error("Google Login Error:", error);
	    }
	}

	// ✅ Run checkGoogleLogin when page loads
	/*window.onload = checkGoogleLogin;*/



    // Simulate checking credentials (you can match with a database in a real app)
    /*if (email === 'user@example.com' && password === 'password123') {
        alert('Login Successful!');
        window.location.href = 'Dashboard.html'; // Redirect to the dashboard after successful login
    } else {
        alert('Invalid email or password!');
    }
});*/
