// Set today's date as default in the date field
document.addEventListener("DOMContentLoaded", function () {
    const today = new Date().toISOString().split("T")[0];

    const expenseDateInput = document.getElementById("expense-date");
    if (expenseDateInput) {
        expenseDateInput.value = today;
    }
});

document.getElementById('add-expense-form').addEventListener('submit', function(e) {
    e.preventDefault();

    const expenseData = {
        name: document.getElementById('expense-name').value.trim(),
        amount: parseFloat(document.getElementById('expense-amount').value),
        date: document.getElementById('expense-date').value,
        category: document.getElementById('expense-category').value,
        type: document.getElementById('type').value,
        comments: document.getElementById('other-details').value.trim()
    };

    const today = new Date().toISOString().split("T")[0];

    // Validations
    if (expenseData.name.length < 3 || !expenseData.date || expenseData.date > today || !expenseData.category || !expenseData.type) {
        alert("Please fill out all required fields correctly.");
        return;
    }

    if (isNaN(expenseData.amount) || expenseData.amount <= 0) {
        alert("Enter a valid positive amount.");
        return;
    }

    console.log("Sending Expense Data:", expenseData);

    // Send data to backend
    fetch('http://localhost:8080/api/expense/addExpense', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(expenseData)
    })
    .then(response => response.json())
    .then(data => {
        alert('Expense added successfully!');
        window.location.href = "Dashboard.html";
    })
    .catch(error => console.error('Error:', error));
});
