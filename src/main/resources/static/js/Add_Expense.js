/*// Set today's date as default in the date field
document.addEventListener("DOMContentLoaded", function () {
    const today = new Date().toISOString().split("T")[0];

    const expenseDateInput = document.getElementById("expense-date");
    if (expenseDateInput) {
        expenseDateInput.value = today;
    }
});*/

document.addEventListener("DOMContentLoaded", function () {
    const userId = localStorage.getItem("userId"); // Get logged-in user ID

    if (!userId) {
        alert("User not logged in! Redirecting to login.");
        window.location.href = "login.html";
    }

    document.getElementById("addExpenseForm").addEventListener("submit", function (event) {
        event.preventDefault();

        const name = document.getElementById("name").value;
        const amount = parseFloat(document.getElementById("amount").value);
        const category = document.getElementById("category").value;
        const type = document.getElementById("type").value;
        const date = document.getElementById("date").value || new Date().toISOString().split("T")[0]; // Default to current date
        const comments = document.getElementById("comments").value;

        if (!name || !amount || !category || !type) {
            alert("Please fill all mandatory fields.");
            return;
        }

        if (amount <= 0) {
            alert("Amount must be a positive value.");
            return;
        }

        fetch(`/api/expense/addExpense/${userId}`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ name, amount, category, type, date, comments })
        })
        .then(response => response.json())
        .then(data => {
            alert("Expense added successfully!");
            window.location.reload();
        })
        .catch(error => console.error("Error:", error));
    });
});
