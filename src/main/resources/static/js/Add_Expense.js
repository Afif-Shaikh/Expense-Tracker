document.addEventListener("DOMContentLoaded", function () {
    // Set today's date as default
    const dateField = document.getElementById("expense-date");
    dateField.value = new Date().toISOString().split("T")[0];

    const form = document.getElementById("add-expense-form");

    form.addEventListener("submit", function (event) {
        event.preventDefault();

        const name = document.getElementById("expense-name").value.trim();
        const amount = parseFloat(document.getElementById("expense-amount").value);
        const date = document.getElementById("expense-date").value;
        const category = document.getElementById("expense-category").value;
        const comments = document.getElementById("other-details").value.trim();

        // Validations
        if (!name || name.length < 2) {
            alert("Please enter a valid expense name (at least 2 characters).");
            return;
        }
        if (isNaN(amount) || amount <= 0) {
            alert("Please enter a valid amount greater than zero.");
            return;
        }
        if (!date) {
            alert("Please select a date.");
            return;
        }

        const expense = {
            name: name,
            amount: amount,
            date: date,
            category: category,
            type: "EXPENSE",
            comments: comments || null
        };

        fetch("/api/transactions", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(expense)
        })
        .then(response => {
            if (!response.ok) {
                return response.json().then(err => {
                    throw err;
                });
            }
            return response.json();
        })
        .then(data => {
            alert("Expense added successfully!");
            window.location.href = "Dashboard.html";
        })
        .catch(error => {
            console.error("Error:", error);
            if (error.details) {
                const messages = error.details.map(d => d.field + ": " + d.message);
                alert("Validation errors:\n" + messages.join("\n"));
            } else {
                alert("Failed to add expense. Please try again.");
            }
        });
    });
});