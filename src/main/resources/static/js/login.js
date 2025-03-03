// Login form validation
document.getElementById('login-form').addEventListener('submit', function(event) {
    event.preventDefault(); // Prevent form submission

    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    // Basic validation
    if (email === '' || password === '') {
        alert('Both fields are required!');
        return;
    }

    // Simulate checking credentials (you can match with a database in a real app)
    if (email === 'user@example.com' && password === 'password123') {
        alert('Login Successful!');
        window.location.href = 'Dashboard.html'; // Redirect to the dashboard after successful login
    } else {
        alert('Invalid email or password!');
    }
});
