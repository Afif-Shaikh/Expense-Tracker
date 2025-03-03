const transactions = [
    { id: 1, name: "Groceries", amount: 1500, date: "2025-02-01", category: "Food", type: "expense" },
    { id: 2, name: "Salary", amount: 50000, date: "2025-02-02", category: "Income", type: "income" },
    { id: 3, name: "Movie Ticket", amount: 350, date: "2025-02-03", category: "Entertainment", type: "expense" },
    { id: 4, name: "Freelance Work", amount: 12000, date: "2025-02-04", category: "Income", type: "income" },
    { id: 5, name: "Electricity Bill", amount: 1000, date: "2025-02-05", category: "Bills", type: "expense" }
];

let filteredTransactions = [...transactions];

// Function to render transactions in the list
function renderTransactions() {
    const transactionsList = document.getElementById("transactions-list");
    transactionsList.innerHTML = "";

    filteredTransactions.forEach(transaction => {
        const li = document.createElement("li");
        li.innerHTML = `
            <div class="transaction-info">
                <strong>${transaction.name}</strong>
                <span>${transaction.date} | ${transaction.category}</span>
            </div>
            <div class="transaction-amount ${transaction.type}">₹${transaction.amount}</div>
            <div class="transaction-actions">
                <button onclick="editTransaction(${transaction.id})">Edit</button>
                <button onclick="confirmDeleteTransaction(${transaction.id})">Delete</button>
            </div>
        `;
        transactionsList.appendChild(li);
    });
}

// Edit transaction (functionality to be added)
function editTransaction(id) {
    alert(`Editing transaction ${id}`);
}

// Confirm and Delete transaction
function confirmDeleteTransaction(id) {
    const confirmation = window.confirm("Are you sure you want to delete this transaction?");

    if (confirmation) {
        deleteTransaction(id);
    }
}

// Delete transaction
function deleteTransaction(id) {
    filteredTransactions = filteredTransactions.filter(transaction => transaction.id !== id);
    renderTransactions();
}

// Initial render
renderTransactions();
