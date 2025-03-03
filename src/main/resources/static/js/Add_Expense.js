// Set today's date as default in the date field
document.addEventListener("DOMContentLoaded", function () {
    const today = new Date().toISOString().split("T")[0]; // Get today's date in YYYY-MM-DD format

    const expenseDateInput = document.getElementById("expense-date");
    if (expenseDateInput) {
        expenseDateInput.value = today; // Set default value if element exists
    }
});

document.getElementById('add-expense-form').addEventListener('submit', function(e) {
    e.preventDefault();

    const expenseName = document.getElementById('expense-name').value.trim();
    const expenseAmount = parseFloat(document.getElementById('expense-amount').value);
    const expenseDate = document.getElementById('expense-date').value;
    const expenseCategory = document.getElementById('expense-category').value;
    const expenseType = document.getElementById('type').value;
    const expenseComment = document.getElementById('other-details').value.trim();
    const today = new Date().toISOString().split("T")[0];

    // Validations
    if (expenseName.length < 3 || !expenseDate || expenseDate > today || !expenseCategory || !expenseType) {
        alert("Please fill out all required fields correctly.");
        return;
    }

    if (isNaN(expenseAmount) || expenseAmount <= 0) {
        alert("Enter a valid positive amount.");
        return;
    }

    /*if (expenseComment.length > 250) {
        alert("Comments must be under 250 characters.");
        return;
    }*/

    console.log({
        expenseName,
        expenseAmount,
        expenseDate,
        expenseCategory,
        expenseType,
        expenseComment
    });

    // Reset the form after submission
    document.getElementById('add-expense-form').reset();
    alert('Expense added successfully!');
    window.location.href = "Dashboard.html";
});
