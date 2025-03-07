document.addEventListener("DOMContentLoaded", function () {
    fetchTransactions();
});

let transactions = [];

function fetchTransactions() {
    let transactionsList = document.getElementById("transactions-list");
    transactionsList.innerHTML = ""; // Clear previous data
    transactions = []; // Reset transactions array

    // Fetch Expenses and Income, then update UI only once
    Promise.all([
        fetch("http://localhost:8080/api/expense/getExpense").then(response => response.json()),
        fetch("http://localhost:8080/api/income/getIncome").then(response => response.json())
    ])
    .then(([expenses, income]) => {
        expenses.forEach(expense => {
            transactions.push({
                id: expense.id,
                name: expense.name,
                amount: expense.amount,
                date: expense.date,
                category: expense.category,
                type: "expense"
            });
        });

        income.forEach(entry => {
            transactions.push({
                id: entry.id,
                name: entry.name,
                amount: entry.amount,
                date: entry.date,
                category: entry.category,
                type: "income"
            });
        });

        renderTransactions(); // Now render only once
    })
    .catch(error => console.error("Error fetching transactions:", error));
}

// Function to render transactions in the list
function renderTransactions() {
    console.log("Rendering transactions...");
    const transactionsList = document.getElementById("transactions-list");
    transactionsList.innerHTML = ""; // Clear previous entries

    // Ensure transactions are sorted (latest first)
    transactions = [...new Map(transactions.map(item => [item.id, item])).values()];

    transactions.sort((a, b) => new Date(b.date) - new Date(a.date));

    transactions.forEach(transaction => {
        const li = document.createElement("li");
        li.innerHTML = `
            <div class="transaction-info">
                <strong>${transaction.name}</strong>
                <span>${transaction.date} | ${transaction.category}</span>
            </div>
            <div class="transaction-amount ${transaction.type}">₹${transaction.amount}</div>
            <div class="transaction-actions">
                <button onclick="editTransaction(${transaction.id}, '${transaction.type}')">Edit</button>
                <button onclick="confirmDeleteTransaction(${transaction.id}, '${transaction.type}')">Delete</button>
            </div>
        `;
        transactionsList.appendChild(li);
    });
}

// Filter Transactions
function filterTransactions() {
    const categoryFilter = document.getElementById("category-filter").value;
    const typeFilter = document.getElementById("type-filter").value;

    filteredTransactions = transactions.filter(transaction => {
        return (categoryFilter === "all" || transaction.category === categoryFilter) &&
               (typeFilter === "all" || transaction.type === typeFilter);
    });

    renderTransactions();
}

// Confirm and Delete transaction
function confirmDeleteTransaction(id, type) {
    const confirmation = window.confirm("Are you sure you want to delete this transaction?");
    if (confirmation) {
        deleteTransaction(id, type);
    }
}

// Delete transaction (calls backend API)
function deleteTransaction(id, type) {
    let endpoint = type === "income" ? "api/income/deleteIncome" : "api/expense/deleteExpense";

    fetch(`http://localhost:8080/${endpoint}/${id}`, {
        method: "DELETE"
    })
    .then(response => {
        if (response.ok) {
            alert("Transaction deleted successfully!");
            fetchTransactions(); // Refresh the list after deletion
        } else {
            alert("Failed to delete transaction.");
        }
    })
    .catch(error => console.error("Error deleting transaction:", error));
}

// Edit transaction (Functionality to be added later)
function editTransaction(id, type) {
    alert(`Editing ${type} transaction with ID: ${id}`);
}

// Event Listeners for Filters
document.getElementById("category-filter").addEventListener("change", filterTransactions);
document.getElementById("type-filter").addEventListener("change", filterTransactions);

// Initial render
fetchTransactions();
