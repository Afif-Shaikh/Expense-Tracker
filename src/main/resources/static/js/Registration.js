// Registration form validation
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
